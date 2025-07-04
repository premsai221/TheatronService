package com.example.TheatronService.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class VideoProcessingResponseMQService {

    private MediaService mediaService;

    public VideoProcessingResponseMQService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @RabbitListener(queues = "video_processing_response_queue")
    public void process(String message) {
        try {
            System.out.println("Received message: " + message);
            String username = message.split("[|]")[0];
            String mediaId = message.split("[|]")[1];
            mediaService.updateMediaProcessStatusToProcessed(mediaId, username);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
