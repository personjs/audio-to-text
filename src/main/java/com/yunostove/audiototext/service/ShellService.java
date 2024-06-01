package com.yunostove.fileprocessoro.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service
public class ShellService {
    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command); // For Unix-based systems, change bash to cmd for Windows.

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("MoviePy")) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Command '{}' exited with non-zero exit code {}", command, exitCode);
            } else {
                log.info("Command '{}' output:\n{}", command, output);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
