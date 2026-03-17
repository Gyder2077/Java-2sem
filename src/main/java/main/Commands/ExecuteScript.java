package main.Commands;

import main.Utils.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ExecuteScript implements Command {
    private final Invoker invoker;

    public ExecuteScript(Invoker invoker) {this.invoker = invoker;}

    @Override
    public void execute() {
        String filename = System.getenv("SCRIPT_FILE");
        if (filename == null || filename.trim().isEmpty()) {
            filename = "script.txt";
            System.out.println("Environment variable 'SCRIPT_FILE' was not set. Using default file: " + filename);
        } else System.out.println("Using file from environment: " + filename);

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            String line;
            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;
                boolean success = invoker.runScriptCommand(line, bufferedReader);
                if (!success) {
                    System.out.println("Command execution error " + lineNumber + ". Script was terminated.");
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("EROR: script file not found: " + filename);
        } catch (IOException e) {
            System.out.println("Input-output exception: " + e.getMessage());
        }
    }
}
