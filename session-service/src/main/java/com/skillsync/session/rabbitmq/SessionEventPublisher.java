package com.skillsync.session.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishSessionBooked(Long sessionId, Long learnerId, Long mentorId, String scheduledAt) {
        publishEvent("SESSION_BOOKED", RabbitMQConfig.BOOKED_KEY, sessionId, learnerId, mentorId, scheduledAt);
    }

    public void publishSessionAccepted(Long sessionId, Long learnerId, Long mentorId, String scheduledAt) {
        publishEvent("SESSION_ACCEPTED", RabbitMQConfig.ACCEPTED_KEY, sessionId, learnerId, mentorId, scheduledAt);
    }

    public void publishSessionRejected(Long sessionId, Long learnerId, Long mentorId, String scheduledAt) {
        publishEvent("SESSION_REJECTED", RabbitMQConfig.REJECTED_KEY, sessionId, learnerId, mentorId, scheduledAt);
    }

    private void publishEvent(String eventType, String routingKey, Long sessionId, Long learnerId, Long mentorId, String scheduledAt) {
        SessionEventDto event = SessionEventDto.builder()
                .sessionId(sessionId)
                .learnerId(learnerId)
                .mentorId(mentorId)
                .eventType(eventType)
                .scheduledAt(scheduledAt)
                .timestamp(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, event);
        log.info("Published event {} for Session ID {}", eventType, sessionId);
    }
}
