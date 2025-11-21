package com.ecommerce.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 *
 * Configures exchanges, queues, and bindings for payment events.
 */
@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_QUEUE = "payment.queue";
    public static final String PAYMENT_ROUTING_KEY = "payment.processed";

    /**
     * Declare payment exchange
     */
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    /**
     * Declare payment queue
     */
    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    /**
     * Bind queue to exchange with routing key
     */
    @Bean
    public Binding paymentBinding() {
        return BindingBuilder
            .bind(paymentQueue())
            .to(paymentExchange())
            .with(PAYMENT_ROUTING_KEY);
    }

    /**
     * JSON message converter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
