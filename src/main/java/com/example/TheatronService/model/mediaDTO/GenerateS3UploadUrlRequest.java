package com.example.TheatronService.model.mediaDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateS3UploadUrlRequest {
    private String filename;
    private String filetype;
}
