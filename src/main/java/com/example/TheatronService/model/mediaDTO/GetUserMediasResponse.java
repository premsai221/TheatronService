package com.example.TheatronService.model.mediaDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetUserMediasResponse {
    private String username;
    List<MediaDetails> videos;
}
