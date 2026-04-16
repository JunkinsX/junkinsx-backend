package com.example.jenkinsx.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "commands")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Commands {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> commandList;
}