package com.example.jenkinsx.util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import java.io.ByteArrayOutputStream;
import lombok.Data;

public class SSHKeyUtils {

    @Data
    public static class SSHKeyPair {
        private final String publicKey;
        private final String privateKey;
    }

    public static SSHKeyPair generateRSAKeyPair() {
        try {
            JSch jsch = new JSch();
            KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 2048);

            ByteArrayOutputStream privOut = new ByteArrayOutputStream();
            kpair.writePrivateKey(privOut);
            String privateKey = privOut.toString();

            ByteArrayOutputStream pubOut = new ByteArrayOutputStream();
            kpair.writePublicKey(pubOut, "JunkinsX");
            String publicKey = pubOut.toString();

            kpair.dispose();
            return new SSHKeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SSH key pair", e);
        }
    }
}
