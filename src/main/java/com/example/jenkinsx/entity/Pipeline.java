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
    private String pipelineName;
    private String pipelineDescription;
    //bundle of ipaddress named
    @OneToMany(cascade = CascadeType.ALL)
    private List<Bundle> ipAddressBundle;
    //task list with names
    @OneToMany(cascade = CascadeType.ALL)
    private List<Task> tasksList;
    //secret variables related to pipeline
    @OneToMany(cascade = CascadeType.ALL)
    private List<Secret> secretList;
    private String publicKey;
    private String privateKey;

    public Pipeline(String pipelineName, String pipelineDescription) {
        this.pipelineName = pipelineName;
        this.pipelineDescription = pipelineDescription;
    }

    public void getIpAddressBundle(List<Bundle> bundleList) {
        this.ipAddressBundle = bundleList;
    }

}
