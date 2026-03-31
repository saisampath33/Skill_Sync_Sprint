package com.skillsync.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.user.dto.UserProfileResponseDto;
import com.skillsync.user.service.interfaces.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private UserProfileController userProfileController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void createProfile_ReturnsCreated() throws Exception {
        when(userProfileService.createProfile(org.mockito.ArgumentMatchers.eq(100L), ArgumentMatchers.any()))
                .thenReturn(UserProfileResponseDto.builder().userId(100L).fullName("John Doe").build());

        mockMvc.perform(post("/users/profile")
                        .header("X-User-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserProfileRequest("John Doe", "Java Dev", "avatar", "Pune", "linkedin"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(100L))
                .andExpect(jsonPath("$.fullName").value("John Doe"));

        verify(userProfileService).createProfile(org.mockito.ArgumentMatchers.eq(100L), ArgumentMatchers.any());
    }

    @Test
    void updateProfile_ReturnsOk() throws Exception {
        when(userProfileService.updateProfile(org.mockito.ArgumentMatchers.eq(101L), ArgumentMatchers.any()))
                .thenReturn(UserProfileResponseDto.builder().userId(101L).fullName("Jane Doe").build());

        mockMvc.perform(put("/users/profile")
                        .header("X-User-Id", 101L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserProfileRequest("Jane Doe", "Mentor", "avatar", "Delhi", "linkedin"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));

        verify(userProfileService).updateProfile(org.mockito.ArgumentMatchers.eq(101L), ArgumentMatchers.any());
    }

    @Test
    void getMyProfile_ReturnsOk() throws Exception {
        when(userProfileService.getProfileByUserId(102L)).thenReturn(UserProfileResponseDto.builder().userId(102L).fullName("Self").build());

        mockMvc.perform(get("/users/profile/me").header("X-User-Id", 102L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Self"));

        verify(userProfileService).getProfileByUserId(102L);
    }

    @Test
    void getProfile_ReturnsOk() throws Exception {
        when(userProfileService.getProfileByUserId(103L)).thenReturn(UserProfileResponseDto.builder().userId(103L).fullName("Public").build());

        mockMvc.perform(get("/users/profile/103"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(103L));

        verify(userProfileService).getProfileByUserId(103L);
    }

    @Test
    void getAllProfiles_ReturnsOk() throws Exception {
        when(userProfileService.getAllProfiles()).thenReturn(List.of(UserProfileResponseDto.builder().userId(104L).fullName("Admin View").build()));

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Admin View"));

        verify(userProfileService).getAllProfiles();
    }

    @Test
    void deleteProfile_ReturnsOk() throws Exception {
        doNothing().when(userProfileService).deleteProfile(105L);

        mockMvc.perform(delete("/users/profile/105"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile deleted successfully"));

        verify(userProfileService).deleteProfile(105L);
    }

    private record UserProfileRequest(
            String fullName,
            String bio,
            String avatarUrl,
            String location,
            String linkedinUrl
    ) {}
}
