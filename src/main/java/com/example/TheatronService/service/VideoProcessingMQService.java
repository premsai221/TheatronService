package com.example.TheatronService.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VideoProcessingMQService {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.request.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    VideoProcessingMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

}
