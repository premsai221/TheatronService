package com.example.TheatronService.controller;

import com.example.TheatronService.model.TheatronUser;
import com.example.TheatronService.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    UserRepo userRepo;

    @GetMapping("/")
    public TheatronUser root() {
        TheatronUser user = userRepo.getUserFromUsername("user");
        System.out.println(user);
        return user;
    }
}
