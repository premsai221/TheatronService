package com.example.TheatronService.model.mediaDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShareMediaResponse {
    private List<String> updatedAccessList;
}
