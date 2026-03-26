package com.skillsync.group.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"groupId", "userId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long userId; // Logical ref to User Service

    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.LEARNER;

    @CreationTimestamp
    private LocalDateTime joinedAt;

    public enum MemberRole {
        ADMIN, LEARNER
    }
}
