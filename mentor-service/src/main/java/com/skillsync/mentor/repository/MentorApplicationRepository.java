package com.skillsync.mentor.repository;

import com.skillsync.mentor.entity.MentorApplication;
import com.skillsync.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorApplicationRepository extends JpaRepository<MentorApplication, Long> {
    List<MentorApplication> findByStatus(Mentor.MentorStatus status);
    boolean existsByUserIdAndStatus(Long userId, Mentor.MentorStatus status);
}
