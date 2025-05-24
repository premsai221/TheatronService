package com.example.TheatronService.model.authModel;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}
