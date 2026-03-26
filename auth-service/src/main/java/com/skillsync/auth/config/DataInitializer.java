package com.skillsync.auth.config;

import com.skillsync.auth.entity.Role;
import com.skillsync.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the roles table on first startup.
 * ROLE_LEARNER, ROLE_MENTOR, ROLE_ADMIN
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        List<String> roles = List.of("ROLE_LEARNER", "ROLE_MENTOR", "ROLE_ADMIN");
        roles.forEach(roleName -> {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
                log.info("Seeded role: {}", roleName);
            }
        });
    }
}
