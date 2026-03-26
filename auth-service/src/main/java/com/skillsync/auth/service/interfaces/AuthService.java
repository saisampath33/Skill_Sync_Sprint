package com.skillsync.auth.service.interfaces;

import com.skillsync.auth.dto.AuthResponseDto;
import com.skillsync.auth.dto.LoginRequestDto;
import com.skillsync.auth.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);
    AuthResponseDto login(LoginRequestDto request);
    AuthResponseDto refreshToken(String refreshToken);
    boolean validateToken(String token);
}
