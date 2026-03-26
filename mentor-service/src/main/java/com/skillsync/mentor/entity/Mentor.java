package com.skillsync.mentor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long userId;        // Logical ref to Auth DB

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorStatus status = MentorStatus.PENDING;

    private Integer experience;

    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal hourlyRate;

    private Double rating = 0.0;

    private Integer totalReviews = 0;

    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum MentorStatus {
        PENDING, APPROVED, REJECTED
    }
}
