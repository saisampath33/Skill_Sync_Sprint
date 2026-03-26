package com.skillsync.mentor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentor_applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String motivation;

    private String resumeUrl;

    private Integer experience;

    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal hourlyRate;

    @Enumerated(EnumType.STRING)
    private Mentor.MentorStatus status = Mentor.MentorStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime submittedAt;
}
