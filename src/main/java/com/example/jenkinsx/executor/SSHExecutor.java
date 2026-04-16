package com.example.jenkinsx.executor;

import com.jcraft.jsch.*;

public class SSHExecutor {

    public static String executeSingleCommand(
            String host,
            String user,
            String privateKey,
            String command
    ) {

        StringBuilder output = new StringBuilder();

        try {
            JSch jsch = new JSch();
            // Sanitize the private key: trim whitespace and ensure it's not null
            if (privateKey == null || privateKey.trim().isEmpty()) {
                return "ERROR: Private key is missing or empty";
            }
            jsch.addIdentity("key", privateKey.trim().getBytes(), null, null);

            Session session = jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000); // 30s timeout

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            channel.setInputStream(null);
            java.io.InputStream in = channel.getInputStream();
            java.io.InputStream err = channel.getErrStream();

            channel.connect();

            byte[] buffer = new byte[1024];

            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) break;
                    output.append(new String(buffer, 0, i));
                }
                while (err.available() > 0) {
                    int i = err.read(buffer, 0, 1024);
                    if (i < 0) break;
                    output.append(new String(buffer, 0, i));
                }

                if (channel.isClosed()) {
                    if (in.available() > 0) continue; 
                    break;
                }
                Thread.sleep(100);
            }

            channel.disconnect();
            session.disconnect();

            int exitStatus = channel.getExitStatus();
            if (exitStatus != 0) {
                return "ERROR: Command exited with status " + exitStatus + "\n" + output.toString();
            }

            return output.toString();

        } catch (Exception e) {
            String msg = e.getMessage();
            if ("Auth fail".equals(msg)) {
                return "ERROR: SSH Authentication Failed. Please verify that the Public Key is added to ~/.ssh/authorized_keys on the target server (" + host + ").";
            }
            return "ERROR: " + msg;
        }
    }
}