package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.LoginRequest;
import com.example.jenkinsx.dto.RegisterRequest;
import com.example.jenkinsx.entity.User;
import com.example.jenkinsx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String register(RegisterRequest dto) {

        User user = new User(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword()
        );

        userRepository.save(user);

        return "User registered";
    }

    public String login(LoginRequest dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow();

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return "Login successful";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}