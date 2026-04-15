package com.example.jenkinsx.dto;

import lombok.Data;

@Data
public class AddGetSecret {
    private String secretName;
    private String secretContent;
}
