package com.example.TheatronService.model.authModel;

import lombok.Data;

@Data
public class UserSignupRequest {
    private String username;
    private String email;
    private String name;
    private String password;
}
