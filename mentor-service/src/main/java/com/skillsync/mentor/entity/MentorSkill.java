package com.skillsync.mentor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mentor_skills",
       uniqueConstraints = @UniqueConstraint(columnNames = {"mentor_id", "skill_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mentorId;

    @Column(nullable = false)
    private Long skillId;   // Logical ref to Skill DB
}
