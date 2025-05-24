package com.example.TheatronService.controller;

import com.example.TheatronService.model.authModel.UserLoginRequest;
import com.example.TheatronService.model.authModel.UserSignupRequest;
import com.example.TheatronService.model.authModel.UserSignupResponse;
import com.example.TheatronService.service.AuthService;
import com.example.TheatronService.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response)  {

        String username = authService.authoriseUser(userLoginRequest);

        String token = jwtService.generateToken(username);

        Cookie cookie = new Cookie("Authorization", token);

        cookie.setMaxAge(jwtService.getJwtExpiration());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);

        return userLoginRequest.getPassword();
    }

    @PostMapping("/signup")
    public UserSignupResponse signupUser(UserSignupRequest userSignupRequest) {
        String username = userSignupRequest.getUsername();
        String email = userSignupRequest.getEmail();
        String name = userSignupRequest.getName();
        String password = userSignupRequest.getPassword();
        if (username == null || username.isEmpty() ||
                email == null || email.isEmpty() ||
                password == null || password.isEmpty() ||
                name == null || name.isEmpty()) {
            return UserSignupResponse.builder().created(false).message("MISSING").build();
        }
        boolean isUsernameTaken = !authService.isUsernameAvailable(username);
        boolean isEmailTaken = !authService.isEmailAvailable(email);
        if (isUsernameTaken || isEmailTaken) {
            return UserSignupResponse.builder()
                    .created(false)
                    .message("RETRY")
                    .email(isEmailTaken ? "Email in use" : "")
                    .username(isUsernameTaken ? "Username in use" : "")
                    .build();
        }
        authService.registerUser(username, email, name, password);
        return UserSignupResponse.builder()
                .created(true)
                .message("DONE")
                .build();
    }
}
