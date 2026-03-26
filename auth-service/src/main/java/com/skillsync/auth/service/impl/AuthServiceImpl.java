package com.skillsync.auth.service.impl;

import com.skillsync.auth.dto.AuthResponseDto;
import com.skillsync.auth.dto.LoginRequestDto;
import com.skillsync.auth.dto.RegisterRequestDto;
import com.skillsync.auth.entity.Role;
import com.skillsync.auth.entity.User;
import com.skillsync.auth.exception.BadRequestException;
import com.skillsync.auth.exception.ResourceNotFoundException;
import com.skillsync.auth.repository.RoleRepository;
import com.skillsync.auth.repository.UserRepository;
import com.skillsync.auth.service.interfaces.AuthService;
import com.skillsync.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken: " + request.getUsername());
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName() != null && !request.getFullName().isBlank() 
                        ? request.getFullName() : request.getUsername())
                .enabled(true)
                .roles(Set.of(role))
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered: email={}, role={}", savedUser.getEmail(), role.getName());

        String accessToken  = jwtUtil.generateAccessToken(savedUser.getId(), role.getName(), savedUser.getFullName());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getId());

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .role(role.getName())
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!user.getEnabled()) {
            throw new BadRequestException("Account is disabled");
        }

        String roleName = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_LEARNER");

        String accessToken  = jwtUtil.generateAccessToken(user.getId(), roleName, user.getFullName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        log.info("User logged in: email={}", user.getEmail());

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .role(roleName)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthResponseDto refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        Long userId = Long.parseLong(jwtUtil.extractSubject(refreshToken));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String roleName = user.getRoles().stream()
                .map(Role::getName).findFirst().orElse("ROLE_LEARNER");

        String newAccessToken = jwtUtil.generateAccessToken(userId, roleName, user.getFullName());

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .role(roleName)
                .userId(userId)
                .email(user.getEmail())
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}
