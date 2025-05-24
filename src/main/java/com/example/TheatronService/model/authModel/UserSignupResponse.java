package com.example.TheatronService.model.authModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignupResponse {
    private boolean created;
    private String message;
    private String email;
    private String username;
}
