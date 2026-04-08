package com.example.junkinsx.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Command {
    @Id
    @GeneratedValue
    Long id;
    @ElementCollection
    List<String> values;
}
