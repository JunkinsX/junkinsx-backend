package com.example.jenkinsx.dto;

import com.example.jenkinsx.entity.Bundle;
import lombok.Data;

import java.util.List;

@Data
public class CreateBundle {
    private Long UserId;
    private List<Bundle> bundleList;
}
