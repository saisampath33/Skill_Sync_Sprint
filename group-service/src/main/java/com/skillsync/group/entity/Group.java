package com.skillsync.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "peer_groups")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long skillId; // Logical ref to Skill Service

    @Column(nullable = false)
    private Long creatorId; // Logical ref to User Service

    private Integer maxMembers = 10;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
