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
            jsch.addIdentity("key", privateKey.getBytes(), null, null);

            Session session = jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            channel.setInputStream(null);
            java.io.InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buffer = new byte[1024];

            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) break;
                    output.append(new String(buffer, 0, i));
                }

                if (channel.isClosed()) break;
                Thread.sleep(100);
            }

            channel.disconnect();
            session.disconnect();

            return output.toString();

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}