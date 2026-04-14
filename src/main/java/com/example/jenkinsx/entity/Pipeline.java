package com.example.jenkinsx.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;

@Entity
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
