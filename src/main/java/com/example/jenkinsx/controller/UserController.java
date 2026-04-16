package com.example.jenkinsx.controller;

import com.example.jenkinsx.dto.LoginRequest;
import com.example.jenkinsx.dto.RegisterRequest;
import com.example.jenkinsx.entity.User;
import com.example.jenkinsx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest dto) {
        return userService.login(dto);
    }
}