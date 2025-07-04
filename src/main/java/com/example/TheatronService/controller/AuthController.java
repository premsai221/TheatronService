package com.example.TheatronService.controller;

import com.example.TheatronService.model.authModel.TestRequestBody;
import com.example.TheatronService.model.authModel.UserLoginRequest;
import com.example.TheatronService.model.authModel.UserSignupRequest;
import com.example.TheatronService.model.authModel.UserSignupResponse;
import com.example.TheatronService.service.AuthService;
import com.example.TheatronService.service.JwtService;
import com.example.TheatronService.service.S3BucketService;
import com.example.TheatronService.service.VideoProcessingMQService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final VideoProcessingMQService videoProcessingMQService;
    private final S3BucketService s3BucketService;

    public AuthController(AuthService authService, JwtService jwtService, VideoProcessingMQService videoProcessingMQService, S3BucketService s3BucketService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.videoProcessingMQService = videoProcessingMQService;
        this.s3BucketService = s3BucketService;
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response)  {

        String username = authService.authoriseUser(userLoginRequest);

        String token = jwtService.generateToken(username);

        Cookie cookie = new Cookie("Authorization", token);

        cookie.setMaxAge(jwtService.getJwtExpiration());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setPath("/");

        response.addCookie(cookie);

        return userLoginRequest.getPassword();
    }

    @PostMapping("/signup")
    public UserSignupResponse signupUser(@RequestBody UserSignupRequest userSignupRequest) {
        System.out.println(userSignupRequest);
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

    @GetMapping("/validate")
    public ResponseEntity<String> validateUserStatus(HttpServletRequest request) {
        Cookie authCookie = WebUtils.getCookie(request, "Authorization");
        if (authCookie != null) {
            String token = authCookie.getValue();
            String username = jwtService.extractUsername(token);
            return ResponseEntity.ok(username);
        }
        return ResponseEntity.badRequest().body("Unauthorised");
    }

    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", "");

        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setPath("/");
        response.addCookie(cookie);
//        return "LOGGED OUT";
    }

    @PostMapping("/test")
    public String testMQ(@RequestBody TestRequestBody testRequestBody) {
        String message = testRequestBody.getMessage();
        videoProcessingMQService.sendMessage(message);
        System.out.println("Send message: " + message);
//        String URL = s3BucketService.generatePreSignedUrl("test");
//        System.out.println(URL);
//        return URL;
        return message;
    }
}
