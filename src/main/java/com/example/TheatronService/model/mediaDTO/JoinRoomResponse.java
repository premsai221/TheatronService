package com.example.TheatronService.model.mediaDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinRoomResponse {
    private boolean roomAvailable;
    private String token;
}
