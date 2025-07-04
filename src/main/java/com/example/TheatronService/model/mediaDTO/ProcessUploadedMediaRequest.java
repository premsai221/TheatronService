package com.example.TheatronService.model.mediaDTO;

import lombok.Data;

@Data
public class ProcessUploadedMediaRequest {
    private String title;
    private String description;
    private long duration;
    private long size;
    private String type;
    private String id;
    private String uploadUrl;
}
