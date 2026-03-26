package com.skillsync.auth.rabbitmq;

import com.skillsync.auth.entity.Role;
import com.skillsync.auth.entity.User;
import com.skillsync.auth.repository.RoleRepository;
import com.skillsync.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorEventListener {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @RabbitListener(queues = RabbitMQConfig.MENTOR_APPROVED_QUEUE)
    @Transactional
    public void handleMentorApproved(MentorEventDto event) {
        log.info("Received MENTOR_APPROVED event for userId: {}", event.getUserId());

        userRepository.findById(event.getUserId()).ifPresentOrElse(user -> {
            Role mentorRole = roleRepository.findByName("ROLE_MENTOR")
                    .orElseThrow(() -> new RuntimeException("ROLE_MENTOR not found in DB"));
            
            user.getRoles().clear();
            user.getRoles().add(mentorRole);
            userRepository.save(user);
            
            log.info("User {} role updated to ROLE_MENTOR", user.getEmail());
        }, () -> log.warn("User with ID {} not found", event.getUserId()));
    }
}
