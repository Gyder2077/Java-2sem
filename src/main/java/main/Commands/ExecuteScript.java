package main.Commands;

import main.Utils.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Класс команда для запуска других команд из скрипта
 */
public class ExecuteScript implements Command {
    private final Invoker invoker;
    private final String filename;
    private final Set<String> executingScripts;

    public ExecuteScript(Invoker invoker, String filename, Set<String> executingScripts) {
        this.invoker = invoker;
        this.filename = filename;
        this.executingScripts = executingScripts;
    }

    @Override
    public void execute() {
        String absPath = new File(filename).getAbsolutePath();
        if (executingScripts.contains(absPath)) {
            System.out.println("ERROR: Recursive script call");
            return;
        }

        executingScripts.add(absPath);

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            int lineNumber = 0;
            Scanner scanner = new Scanner(Files.newInputStream(Paths.get(filename)));
            invoker.setScanner(scanner);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                lineNumber++;
                try {
                    scanner.nextLine();
                } catch (Exception e) {
                    System.out.println("Script executed successfully");
                    System.out.println();
                    break;
                }
                boolean success = invoker.runCommand(line);

                if (!success) {
                    System.out.println("Command execution error " + lineNumber + ". Script was terminated.");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: script file not found: " + filename);
        } catch (IOException e) {
            System.out.println("Input-output exception: " + e.getMessage());
        } finally {
            invoker.setScanner(new Scanner(System.in));
            executingScripts.remove(absPath);
        }
    }
}
