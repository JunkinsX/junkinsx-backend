package com.example.jenkinsx.executor;

import com.jcraft.jsch.*;

public class SSHExecutor {

    public static void executeSingleCommand(
            String host,
            String user,
            String privateKey,
            String command
    ) {

        try {
            JSch jsch = new JSch();
            jsch.addIdentity("key", privateKey.getBytes(), null, null);

            Session session = jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            channel.connect();

            while (!channel.isClosed()) {
                Thread.sleep(100);
            }

            int exitStatus = channel.getExitStatus();

            if (exitStatus != 0) {
                throw new RuntimeException("Command failed on " + host);
            }

            channel.disconnect();
            session.disconnect();

        } catch (Exception e) {
            throw new RuntimeException("SSH failed: " + e.getMessage());
        }
    }
}