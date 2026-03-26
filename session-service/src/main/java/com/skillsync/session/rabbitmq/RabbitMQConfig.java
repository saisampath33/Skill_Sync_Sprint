package com.skillsync.session.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "skillsync.exchange";

    public static final String SESSION_BOOKED_QUEUE   = "session.booked.queue";
    public static final String SESSION_ACCEPTED_QUEUE = "session.accepted.queue";
    public static final String SESSION_REJECTED_QUEUE = "session.rejected.queue";

    public static final String BOOKED_KEY   = "session.booked";
    public static final String ACCEPTED_KEY = "session.accepted";
    public static final String REJECTED_KEY = "session.rejected";

    @Bean public TopicExchange exchange() { return new TopicExchange(EXCHANGE, true, false); }

    @Bean public Queue bookedQueue()   { return new Queue(SESSION_BOOKED_QUEUE, true); }
    @Bean public Queue acceptedQueue() { return new Queue(SESSION_ACCEPTED_QUEUE, true); }
    @Bean public Queue rejectedQueue() { return new Queue(SESSION_REJECTED_QUEUE, true); }

    @Bean public Binding bookedBinding(@Qualifier("bookedQueue") Queue bookedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(bookedQueue).to(exchange).with(BOOKED_KEY);
    }
    @Bean public Binding acceptedBinding(@Qualifier("acceptedQueue") Queue acceptedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(acceptedQueue).to(exchange).with(ACCEPTED_KEY);
    }
    @Bean public Binding rejectedBinding(@Qualifier("rejectedQueue") Queue rejectedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(rejectedQueue).to(exchange).with(REJECTED_KEY);
    }

    @Bean public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
