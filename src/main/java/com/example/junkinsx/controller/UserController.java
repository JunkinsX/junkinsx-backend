package com.example.junkinsx.controller;


import com.example.junkinsx.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest registerRequest){
        return service.register(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
    }
    public User login(@RequestBody LoginRequest loginRequest){
        return service.login(loginRequest.getEmail(), loginRequest.getPassword());
    }
}
