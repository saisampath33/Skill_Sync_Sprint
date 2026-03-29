package com.skillsync.mentor.service.impl;

import com.skillsync.mentor.dto.MentorApplicationRequestDto;
import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.dto.UserProfileResponseDto;
import com.skillsync.mentor.entity.*;
import com.skillsync.mentor.exception.BadRequestException;
import com.skillsync.mentor.exception.ResourceNotFoundException;
import com.skillsync.mentor.feign.UserFeignClient;
import com.skillsync.mentor.mapper.MentorMapper;
import com.skillsync.mentor.rabbitmq.MentorEventPublisher;
import com.skillsync.mentor.repository.*;
import com.skillsync.mentor.service.interfaces.MentorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final MentorApplicationRepository applicationRepository;
    private final MentorSkillRepository mentorSkillRepository;
    private final MentorAvailabilityRepository availabilityRepository;
    private final UserFeignClient userFeignClient;
    private final MentorEventPublisher eventPublisher;
    private final MentorMapper mentorMapper;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mentors", allEntries = true),
        @CacheEvict(value = "mentorSearch", allEntries = true)
    })
    public MentorResponseDto applyAsMentor(Long userId, String fullName, MentorApplicationRequestDto request) {
        if (mentorRepository.existsByUserId(userId)) {
            throw new BadRequestException("User already has a mentor profile");
        }

        MentorApplication application = MentorApplication.builder()
                .userId(userId)
                .motivation(request.getMotivation())
                .resumeUrl(request.getResumeUrl())
                .experience(request.getExperience())
                .hourlyRate(request.getHourlyRate())
                .status(Mentor.MentorStatus.PENDING)
                .build();
        applicationRepository.save(application);

        Mentor mentor = Mentor.builder()
                .userId(userId)
                .status(Mentor.MentorStatus.PENDING)
                .experience(request.getExperience())
                .hourlyRate(request.getHourlyRate())
                .rating(0.0)
                .totalReviews(0)
                .build();
        Mentor saved = mentorRepository.save(mentor);

        if (request.getSkillIds() != null) {
            request.getSkillIds().forEach(skillId ->
                mentorSkillRepository.save(MentorSkill.builder()
                    .mentorId(saved.getId())
                    .skillId(skillId)
                    .build())
            );
        }

        log.info("Mentor application created for userId: {}", userId);
        return enrichAndMap(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mentor", key = "#mentorId"),
        @CacheEvict(value = "mentors", allEntries = true),
        @CacheEvict(value = "mentorSearch", allEntries = true)
    })
    public MentorResponseDto approveMentor(Long mentorId) {
        Mentor mentor = getMentorEntityById(mentorId);
        mentor.setStatus(Mentor.MentorStatus.APPROVED);
        mentor.setApprovedAt(LocalDateTime.now());
        Mentor saved = mentorRepository.save(mentor);
        eventPublisher.publishMentorApproved(mentorId, mentor.getUserId());
        log.info("Mentor approved: mentorId={}", mentorId);
        return enrichAndMap(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mentor", key = "#mentorId"),
        @CacheEvict(value = "mentors", allEntries = true)
    })
    public MentorResponseDto rejectMentor(Long mentorId) {
        Mentor mentor = getMentorEntityById(mentorId);
        mentor.setStatus(Mentor.MentorStatus.REJECTED);
        return enrichAndMap(mentorRepository.save(mentor));
    }

    @Override
    @Cacheable(value = "mentor", key = "#mentorId")
    public MentorResponseDto getMentorById(Long mentorId) {
        log.info("[CACHE MISS] Fetching mentor {} from DB", mentorId);
        return enrichAndMap(getMentorEntityById(mentorId));
    }

    @Override
    public MentorResponseDto getMentorByUserId(Long userId) {
        Mentor mentor = mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found for userId: " + userId));
        return enrichAndMap(mentor);
    }

    @Override
    @Cacheable(value = "mentorSearch", key = "'skill_' + #skillId + '_rating_' + #minRating")
    public List<MentorResponseDto> searchMentors(Long skillId, Double minRating) {
        log.info("[CACHE MISS] Searching mentors: skillId={}, minRating={}", skillId, minRating);
        List<Mentor> mentors = mentorRepository.findByStatus(Mentor.MentorStatus.APPROVED);

        if (skillId != null) {
            List<Long> mentorIdsWithSkill = mentorSkillRepository.findBySkillId(skillId)
                    .stream().map(MentorSkill::getMentorId).collect(Collectors.toList());
            mentors = mentors.stream()
                    .filter(m -> mentorIdsWithSkill.contains(m.getId()))
                    .collect(Collectors.toList());
        }
        if (minRating != null) {
            mentors = mentors.stream()
                    .filter(m -> m.getRating() >= minRating)
                    .collect(Collectors.toList());
        }

        return mentors.stream().map(this::enrichAndMap).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "mentors", key = "'approved'")
    public List<MentorResponseDto> getAllApprovedMentors() {
        log.info("[CACHE MISS] Fetching all approved mentors from DB");
        return mentorRepository.findByStatus(Mentor.MentorStatus.APPROVED)
                .stream().map(this::enrichAndMap).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MentorAvailability addAvailability(Long userId, Long mentorId, MentorAvailability availability) {
        Mentor mentor = getMentorEntityById(mentorId);
        if (!mentor.getUserId().equals(userId)) {
            throw new BadRequestException("Not authorized to manage availability for this mentor");
        }
        availability.setMentorId(mentorId);
        return availabilityRepository.save(availability);
    }

    @Override
    public List<MentorAvailability> getAvailability(Long mentorId) {
        return availabilityRepository.findByMentorId(mentorId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "mentor", key = "#mentorId")
    public void updateRating(Long mentorId, Double newRating) {
        Mentor mentor = getMentorEntityById(mentorId);
        mentor.setRating(newRating);
        mentor.setTotalReviews(mentor.getTotalReviews() + 1);
        mentorRepository.save(mentor);
    }

    // ---- Helpers ----

    private Mentor getMentorEntityById(Long mentorId) {
        return mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found: " + mentorId));
    }

    private MentorResponseDto enrichAndMap(Mentor mentor) {
        List<Long> skillIds = mentorSkillRepository.findByMentorId(mentor.getId())
                .stream().map(MentorSkill::getSkillId).collect(Collectors.toList());

        UserProfileResponseDto profile = null;
        try {
            profile = userFeignClient.getUserProfile(mentor.getUserId());
        } catch (Exception e) {
            log.warn("Could not fetch user profile for userId {}: {}", mentor.getUserId(), e.getMessage());
        }

        return mentorMapper.toDto(mentor, skillIds, profile);
    }
}
