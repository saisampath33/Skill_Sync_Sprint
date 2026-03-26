package com.skillsync.group.repository;

import com.skillsync.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findBySkillId(Long skillId);
    List<Group> findByNameContainingIgnoreCase(String name);
    List<Group> findByCreatorId(Long creatorId);
}
