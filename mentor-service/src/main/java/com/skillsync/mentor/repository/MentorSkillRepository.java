package com.skillsync.mentor.repository;

import com.skillsync.mentor.entity.MentorSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorSkillRepository extends JpaRepository<MentorSkill, Long> {
    List<MentorSkill> findByMentorId(Long mentorId);
    List<MentorSkill> findBySkillId(Long skillId);
    void deleteByMentorId(Long mentorId);
}
