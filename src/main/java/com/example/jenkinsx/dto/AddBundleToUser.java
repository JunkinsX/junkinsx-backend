package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Bundle;
import lombok.Data;

@Data
public class AddBundleToUser {
    private Long userId;
    private Bundle bundle;
}
