package com.example.jenkinsx.entity;

import jakarta.persistence.*;

import java.util.List;
import lombok.*;

@Entity
@Table(name = "pipelines")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pipeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pipelineDescription;
    //bundle of ipaddress named
    private List<Bundle> ipAddressBundle;
    //task list with names
    private List<Task> tasksList;
    //secret variables related to pipeline
    private List<Secret> secretList;
    private String publicKey;
}
