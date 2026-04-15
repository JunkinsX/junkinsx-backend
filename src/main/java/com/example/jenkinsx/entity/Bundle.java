package com.example.jenkinsx.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "bundles")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String bundleName;
    private String bundleDescription;

    @ElementCollection
    private List<String> ipAddresses;
}