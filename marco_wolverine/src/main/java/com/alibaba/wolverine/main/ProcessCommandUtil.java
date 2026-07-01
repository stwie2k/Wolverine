package com.alibaba.wolverine.main;

import com.alibaba.wolverine.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ProcessCommandUtil {
    public static final String PROCESS_PREFIX = ":";

    public ProcessCommandUtil() {
    }

    public static void executeCommand(File commandEnvironmentPath, Map commandParams, String[] commandArray) {
        if (commandArray.length > 0) {
            ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
            String envPath = System.getenv("PATH");
            if (envPath != null) {
                String[] split = envPath.split(":");
                int length = split.length;

                for(int i = 0; i < length; ++i) {
                    File file2 = new File(split[i], "sh");
                    if (file2.exists()) {
                        String commandPath = file2.getPath();
                        LogUtil.logD(commandPath);
                        processBuilder.command(commandPath).redirectErrorStream(true);
                        break;
                    }
                }
            }

            processBuilder.directory(commandEnvironmentPath);
            Map<String, String> environment = processBuilder.environment();
            environment.putAll(System.getenv());
            if (commandParams != null) {
                environment.putAll(commandParams);
            }

            Process process = null;

            try {
                process = processBuilder.start();
            } catch (IOException e) {
            }

            try {
                OutputStream outputStream = process.getOutputStream();
                String[] var17 = commandArray;
                int var18 = commandArray.length;

                for(int var10 = 0; var10 < var18; ++var10) {
                    String command = var17[var10];
                    LogUtil.logI(command);
                    if (command.endsWith("\n")) {
                        outputStream.write(command.getBytes());
                    } else {
                        outputStream.write((command + "\n").getBytes());
                    }
                }

                outputStream.write("exit 156".getBytes());
                outputStream.flush();
                process.waitFor();
            } catch (InterruptedException | IOException e) {
            }
        }

    }
}
