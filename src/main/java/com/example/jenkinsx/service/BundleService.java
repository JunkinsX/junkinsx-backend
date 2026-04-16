package com.example.jenkinsx.service;

import com.example.jenkinsx.dto.AddBundleToUser;
import com.example.jenkinsx.dto.CreateBundle;
import com.example.jenkinsx.entity.Bundle;
import com.example.jenkinsx.entity.User;
import com.example.jenkinsx.repository.BundleRepository;
import com.example.jenkinsx.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BundleService {
    private BundleRepository bundleRepository;
    private UserRepository userRepository;
    public BundleService(BundleRepository bundleRepository, UserRepository userRepository){
        this.bundleRepository = bundleRepository;
        this.userRepository = userRepository;
    }
    public List<Bundle> createBundle(CreateBundle createBundle){
        User user = userRepository.findById(createBundle.getUserId()).orElseThrow(()->new RuntimeException("User not found!"));
        if (createBundle.getBundleList() != null) {
            user.getIpaddressBundles().addAll(createBundle.getBundleList());
        }
        userRepository.save(user);
        return user.getIpaddressBundles();
    }
    public List<Bundle> addBundleToUser(AddBundleToUser addBundleToUser){
        User user = userRepository.findById(addBundleToUser.getUserId()).orElseThrow(()->new RuntimeException("User not found!"));
        user.addBundle(addBundleToUser.getBundle());
        userRepository.save(user);
        return user.getIpaddressBundles();
    }
    public List<Bundle> getBundle(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        return user.getIpaddressBundles();
    }
}
