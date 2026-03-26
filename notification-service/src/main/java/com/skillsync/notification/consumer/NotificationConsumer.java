package com.skillsync.notification.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class NotificationConsumer {

    /**
     * Consumes all events bound to the notification.main.queue.
     */
    @RabbitListener(queues = "notification.main.queue")
    public void handleNotificationEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        log.info("🔔 NEW NOTIFICATION RECEIVED: [{}]", eventType);
        
        switch (eventType) {
            case "MENTOR_APPROVED":
                log.info("Processing mentor approval notification for userId: {}", event.get("userId"));
                break;
            case "SESSION_BOOKED":
                log.info("Processing session booking notification for sessionId: {}", event.get("sessionId"));
                break;
            case "SESSION_ACCEPTED":
                log.info("Processing session accepted notification for sessionId: {}", event.get("sessionId"));
                break;
            case "SESSION_REJECTED":
                log.info("Processing session rejected notification for sessionId: {}", event.get("sessionId"));
                break;
            default:
                log.warn("Unknown event type received: {}", eventType);
        }
        
        log.info("Notification data: {}", event);
    }
}
