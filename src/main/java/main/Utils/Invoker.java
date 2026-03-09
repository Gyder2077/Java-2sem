package main.Utils;

import main.Commands.Add;
import main.Commands.Clear;
import main.Commands.Info;

import java.util.*;

public class Invoker {
    private final Scanner scanner = new Scanner(System.in);
    private final ArrayDeque<Command> history = new ArrayDeque<>();
    private Map<String, CommandInfo> commands = new HashMap<>();
    private final MyCollection receiver;

    public Invoker(MyCollection myCollection) {
        receiver = myCollection;

        commands.put("info", new CommandInfo("info",
                "Shows info about the collection", args -> {
                    if (args.length < 1) {
                        return new Info(receiver);
                    }
                    System.out.println("ERROR: No argument should be enter for 'info' command");
                    return null;
                }));

        commands.put("show", new CommandInfo("show", "Shows all elements in the collection",
                args -> {
                    if (args.length < 1) {
                        return new Add(receiver);
                    }
                    System.out.println("ERROR: No argument should be enter for 'show' command");
                    return null;
                }));

        commands.put("add", new CommandInfo("add",
                "Adds the element with following parameters th the collection", args -> {
                    if (args.length < 1) {
                        return new Add(receiver);
                    }
                    System.out.println("ERROR: No argument should be enter for 'add' command");
                    return null;
                }));

        commands.put("clear", new CommandInfo("clear", "Clears the collection",
                args -> {
                    if (args.length < 1) {
                        return new Clear(receiver);
                    }
                    System.out.println("ERROR: No argument should be enter for 'clear' command");
                    return null;
                }));

    }

    public void run() {
        while (true) {
            System.out.print(">> ");
        }
    }

    private boolean validate(String prompt) {
        System.out.print(prompt + " (Yes/No): ");
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("y")) {
                return true;
            }

            if (input.equals("no") || input.equals("n")) {
                return false;
            }
            System.out.println("Enter 'Yes' or 'No', please (Y/N is OK).");
        }
    }
}
