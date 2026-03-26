package com.skillsync.auth.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE            = "skillsync.exchange";
    public static final String MENTOR_APPROVED_QUEUE = "auth.mentor.approved.queue";
    public static final String MENTOR_APPROVED_KEY   = "mentor.approved";

    @Bean public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean public Queue mentorApprovedQueue() {
        return new Queue(MENTOR_APPROVED_QUEUE, true);
    }

    @Bean public Binding mentorApprovedBinding(Queue mentorApprovedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(mentorApprovedQueue).to(exchange).with(MENTOR_APPROVED_KEY);
    }

    @Bean public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
