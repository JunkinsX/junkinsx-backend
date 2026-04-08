package com.example.junkinsx.service;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class SshKeyService {
    public Map<String, String> generateKeyPair(Long id) throws Exception{
        String path = "keys/pipeline-" + id;
        ProcessBuilder pb = new ProcessBuilder(
                "ssh-keygen",
                "-t", "rsa",
                "-b", "4096",
                "-f", path,
                "-N", ""
        );
        pb.start().waitFor();
        String privateKey = Files.readString(Path.of(path));
        String publicKey = Files.readString(Path.of(path + ".pub"));
        return Map.of(
                "privateKey", privateKey,
                "publicKey", publicKey
        );
    }
}
