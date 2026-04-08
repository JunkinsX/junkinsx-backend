package com.example.junkinsx.service;

import com.example.junkinsx.model.Pipeline;
import com.example.junkinsx.repository.PipelineRepository;
import com.example.junkinsx.security.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PipelineService {
    @Autowired
    private PipelineRepository repo;

    @Autowired
    private SshKeyService sshKeyService;

    public Pipeline createPipeline(Pipeline pipeline) throws Exception {
        Pipeline saved = repo.save(pipeline);
        Map<String, String> keys = sshKeyService.generateKeyPair(saved.getId());
        String encryptedKey = EncryptionUtil.encrypt(keys.get("privateKey"));
        saved.setPrivateKey(encryptedKey);
        saved.setPublicKey(keys.get("publicKey"));
        saved.setWebhookSecret(UUID.randomUUID().toString());
        return repo.save(saved);
    }
    public Pipeline getPipeline(Long id){
        return repo.findById(id).orElseThrow();
    }
    public Pipeline getDecryptedPipeline(Long id) throws Exception {
        Pipeline p = repo.findById(id).orElseThrow();
        p.setPrivateKey(EncryptionUtil.decrypt(p.getPrivateKey()));
        return p;
    }
    public List<Pipeline> getAll(){
        return repo.findAll();
    }
}
