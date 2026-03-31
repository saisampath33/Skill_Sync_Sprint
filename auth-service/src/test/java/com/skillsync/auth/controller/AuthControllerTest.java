package com.skillsync.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.auth.dto.AuthResponseDto;
import com.skillsync.auth.service.interfaces.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void register_ReturnsCreated() throws Exception {
        AuthResponseDto response = AuthResponseDto.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .email("learner@skillsync.com")
                .userId(11L)
                .role("ROLE_LEARNER")
                .expiresIn(3600L)
                .build();

        when(authService.register(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("learner1", "learner@skillsync.com", "secret1", "Learner One", "ROLE_LEARNER"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.email").value("learner@skillsync.com"));

        verify(authService).register(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void login_ReturnsOk() throws Exception {
        AuthResponseDto response = AuthResponseDto.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .email("mentor@skillsync.com")
                .userId(21L)
                .role("ROLE_MENTOR")
                .expiresIn(3600L)
                .build();

        when(authService.login(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("mentor@skillsync.com", "secret1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_MENTOR"))
                .andExpect(jsonPath("$.userId").value(21L));

        verify(authService).login(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void refresh_ReturnsOk() throws Exception {
        AuthResponseDto response = AuthResponseDto.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();

        when(authService.refreshToken("refresh-token")).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));

        verify(authService).refreshToken("refresh-token");
    }

    @Test
    void validate_ReturnsTokenValidity() throws Exception {
        when(authService.validateToken("jwt-token")).thenReturn(true);

        mockMvc.perform(get("/auth/validate")
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        verify(authService).validateToken("jwt-token");
    }

    @Test
    void deleteUser_ReturnsOk() throws Exception {
        doNothing().when(authService).deleteUser(31L);

        mockMvc.perform(delete("/auth/users/31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User account deleted successfully"));

        verify(authService).deleteUser(31L);
    }

    private record RegisterRequest(
            String username,
            String email,
            String password,
            String fullName,
            String role
    ) {}

    private record LoginRequest(String email, String password) {}
}
