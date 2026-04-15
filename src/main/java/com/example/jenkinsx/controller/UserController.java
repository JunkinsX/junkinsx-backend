package com.example.jenkinsx.controller;

import com.example.jenkinsx.dto.LoginRequest;
import com.example.jenkinsx.dto.RegisterRequest;
import com.example.jenkinsx.entity.User;
import com.example.jenkinsx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping()
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
    @PostMapping("/register")
    public String register(RegisterRequest register){
        return userService.register(register);
    }
    @PostMapping("/login")
    public String login(LoginRequest login){
        return userService.login(login);
    }

}
