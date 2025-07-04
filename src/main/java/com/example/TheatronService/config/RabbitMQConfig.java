package com.example.TheatronVideoProcessingService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.request.name}")
    private String videoProcessingRequestQueue;
    @Value("${rabbitmq.request.key}")
    private String videoProcessingRequestRoutingKey;

    @Value("${rabbitmq.queue.response.name}")
    private String videoProcessingResponseQueue;
    @Value("${rabbitmq.response.key}")
    private String videoProcessingResponseRoutingKey;

    @Value("${rabbitmq.exchange}")
    private String videoProcessingExchange;

    @Bean
    public Queue requestQueue() {
        return new Queue(videoProcessingRequestQueue);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(videoProcessingResponseQueue);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(videoProcessingExchange);
    }

    @Bean
    public Binding requestBinding() {
        return BindingBuilder
                .bind(requestQueue())
                .to(exchange())
                .with(videoProcessingRequestRoutingKey);
    }

    @Bean
    public Binding responseBinding() {
        return BindingBuilder
                .bind(responseQueue())
                .to(exchange())
                .with(videoProcessingResponseRoutingKey);
    }

}
