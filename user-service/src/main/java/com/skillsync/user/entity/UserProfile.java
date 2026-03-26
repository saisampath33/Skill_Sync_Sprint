package com.skillsync.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long userId;           // Logical ref to Auth DB

    @Column(length = 150)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String avatarUrl;

    @Column(length = 100)
    private String location;

    @Column(length = 255)
    private String linkedinUrl;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
