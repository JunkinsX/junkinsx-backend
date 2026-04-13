package com.example.jenkinsx.entity;

import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long username;
    private Long email;
    private Long password;
}
