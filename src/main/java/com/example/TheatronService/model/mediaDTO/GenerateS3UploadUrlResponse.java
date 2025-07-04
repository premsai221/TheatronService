package com.example.TheatronService.model.mediaDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateS3UploadUrlResponse {
    private String uploadUrl;
    private String objectId;
}
