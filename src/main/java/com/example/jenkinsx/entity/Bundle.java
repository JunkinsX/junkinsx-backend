package com.example.jenkinsx.entity;

import jakarta.persistence.*;

import java.util.List;
import lombok.*;

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
    private String user;
    private String bundleName;
    private String bundleDescription;
    //list of ipaddress
    @ElementCollection
    private List<String> ipAddresses;
}
