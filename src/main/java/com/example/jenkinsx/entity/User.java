package com.example.jenkinsx.entity;

import jakarta.persistence.*;
import lombok.*;

import java.nio.channels.Pipe;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Pipeline> pipelineList;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Bundle> ipaddressBundles;

    public User(String username, String email, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public void addBundle(Bundle bundle) {
        ipaddressBundles.add(bundle);
    }
}
