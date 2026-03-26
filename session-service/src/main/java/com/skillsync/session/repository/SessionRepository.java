package com.skillsync.session.repository;

import com.skillsync.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByLearnerId(Long learnerId);
    List<Session> findByMentorId(Long mentorId);
}
