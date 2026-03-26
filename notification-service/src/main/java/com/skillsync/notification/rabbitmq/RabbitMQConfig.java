package com.skillsync.notification.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "skillsync.exchange";

    // Matching Queues (Must match the ones used in publishers if they are to be consumed here)
    public static final String NOTIFICATION_QUEUE = "notification.main.queue";

    // Routing Keys (Must match exactly)
    public static final String MENTOR_APPROVED_KEY = "mentor.approved";
    public static final String SESSION_BOOKED_KEY   = "session.booked";
    public static final String SESSION_ACCEPTED_KEY = "session.accepted";
    public static final String SESSION_REJECTED_KEY = "session.rejected";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    // Binding the one notification queue to multiple routing keys to centralize consumption
    @Bean
    public Binding mentorApprovedBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(MENTOR_APPROVED_KEY);
    }

    @Bean
    public Binding sessionBookedBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(SESSION_BOOKED_KEY);
    }

    @Bean
    public Binding sessionAcceptedBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(SESSION_ACCEPTED_KEY);
    }

    @Bean
    public Binding sessionRejectedBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(SESSION_REJECTED_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
