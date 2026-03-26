package com.skillsync.mentor.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishMentorApproved(Long mentorId, Long userId) {
        MentorEventDto event = MentorEventDto.builder()
                .mentorId(mentorId)
                .userId(userId)
                .eventType("MENTOR_APPROVED")
                .timestamp(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.MENTOR_APPROVED_KEY,
                event);

        log.info("Published MENTOR_APPROVED event for mentorId: {}", mentorId);
    }
}
