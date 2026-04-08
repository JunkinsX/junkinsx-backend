package com.example.junkinsx.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Entity
public class Command {
    @Id
    @GeneratedValue
    Long id;
    @ElementCollection
    List<String> values;
}
