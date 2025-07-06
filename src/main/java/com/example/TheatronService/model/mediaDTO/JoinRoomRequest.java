package com.example.TheatronService.model.mediaDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinRoomRequest {
    private String mediaId;
    private String roomName;
}
