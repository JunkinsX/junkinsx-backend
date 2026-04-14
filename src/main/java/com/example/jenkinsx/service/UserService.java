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
    private UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public String register(RegisterRequest register){
        if(userRepository.findByEmail(register.getEmail()).isPresent()){
            return "Email already exists";
        }
        User user = new User(register.getUsername(), register.getEmail(), register.getPassword());
        userRepository.save(user);
        return "User registered successfully";
    }
    public String login(LoginRequest login){
        User user = userRepository.findByEmail(login.getEmail()).orElseThrow(()->new RuntimeException("User not found"));
        if(!login.getPassword().equals(user.getPassword())){
            return "Invalid password";
        }
        return "login successful";
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
