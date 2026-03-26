package com.skillsync.session.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long learnerId;  // Logical ref to Auth DB

    @Column(nullable = false)
    private Long mentorId;   // Logical ref to Mentor DB

    @Column(nullable = false)
    private Long skillId;    // Logical ref to Skill DB

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status = SessionStatus.REQUESTED;

    private LocalDateTime scheduledAt;

    private Integer durationMin;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum SessionStatus {
        REQUESTED, ACCEPTED, REJECTED, COMPLETED, CANCELLED
    }
}
