package com.example.jenkinsx.entity;

import jakarta.persistence.*;

import java.util.List;
import lombok.*;

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
    private List<String> commandList;
}
