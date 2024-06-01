package com.yunostove.fileprocessoro.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Service
public class TranscribeService {
    @Value("${app.transcribe-script.location}")
    private String transcribeScriptLocation;

    public String transcribe(String filepath) {
        try {
            // Command to run Python script
            ProcessBuilder pb = new ProcessBuilder("python3", transcribeScriptLocation, filepath);
            pb.redirectErrorStream(true);
            
            // Start the process
            Process process = pb.start();

            // Read output from Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Check if the process terminated successfully
            if (exitCode == 0) {
                return "Python script executed successfully. Output: " + output.toString();
            } else {
                return "Python script execution failed with exit code " + exitCode;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }
}
