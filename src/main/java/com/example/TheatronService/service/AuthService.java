package com.example.TheatronService.service;

import com.example.TheatronService.model.TheatronUser;
import com.example.TheatronService.model.authModel.UserLoginRequest;
import com.example.TheatronService.repo.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public String authoriseUser(UserLoginRequest userLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword())
        );

        return userLoginRequest.getUsername();
    }

    public boolean isUsernameAvailable(String username) {
        UserDetails user = userRepo.getUserFromUsername(username);
        return user == null;
    }

    public boolean isEmailAvailable(String email) {
        return !userRepo.isEmailTaken(email);
    }

    public void registerUser(String username, String email, String name, String password) {
        TheatronUser user = new TheatronUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        userRepo.addUser(user);
    }

}
