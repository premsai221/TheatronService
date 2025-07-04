package com.example.TheatronService.model.mediaDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MediaDetails {
    private String owner;
    private String id;
    private String title;
    private String description;
    private long duration;
    private String thumbnail;
    private String status;
    private String url;
    private String uploadedOn; // UTC timestamp
    private List<String> externalUserAccessList;
}
