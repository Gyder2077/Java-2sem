package main.Utils;

import main.Commands.*;
import main.Given.Enums.TicketType;

import java.util.*;

public class Invoker {
    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, CommandInfo> commands = new HashMap<>();
    private final MyCollection receiver;

    public Invoker(MyCollection myCollection) {
        receiver = myCollection;

        commands.put("help", new CommandInfo("help", "Shows list of available commands",
                args -> {
                    if (args.length < 1) {
                        return new Help(commands);
                    }
                    System.out.println("The 'help' command requires no arguments.");
                    return null;
                }));

        commands.put("info", new CommandInfo("info", "Shows info about the collection",
                args -> {
                    if (args.length < 1) {
                        return new Info(receiver);
                    }
                    System.out.println("The 'info' command requires no arguments.");
                    return null;
                }));
        
        commands.put("history", new CommandInfo("history", "Shows last 8 commands used",
                args -> {
                    if (args.length < 1) {
                        return new History(receiver.getHistory());
                    }
                    System.out.println("The 'history' command requires no arguments.");
                    return null;
                }));

        commands.put("show", new CommandInfo("show", "Shows all elements in the collection",
                args -> {
                    if (args.length < 1) {
                        return new Show(receiver);
                    }
                    System.out.println("The 'show' command requires no arguments.");
                    return null;
                }));

        commands.put("print_descending", new CommandInfo("print_descending",
                "Shows all elements sorted in the reverse", args -> {
                    if (args.length < 1) {
                        return new PrintDescending(receiver);
                    }
                    System.out.println("The 'print_descending' command requires no arguments.");
                    return null;
                }));

        commands.put("filter_contains_name", new CommandInfo("filter_contains_name",
                "Shows elements whose name field value contains the specified substring", args -> {
                    if (args.length != 1) {
                        System.out.println("One argument 'name' is required");
                        return null;
                    } return new FilterContainsName(receiver, args[0]);
                }));

        commands.put("filter_less_than_type", new CommandInfo("filter_less_than_type",
                "Shows elements whose type field value if less than the argument", args -> {
                    if (args.length != 1) {
                        System.out.println("One argument 'type' is required");
                        return null;
                    }
                    try {
                        return new FilterLessThanType(receiver, TicketType.valueOf(args[0]));
                    } catch (IllegalArgumentException e) {
                        System.out.println("The 'type' argument must be one of the Ticket types");
                        return null;
                    }
                }));

        commands.put("add", new CommandInfo("add",
                "Adds the element with following parameters to the collection", args -> {
                    if (args.length < 1) {
                        return new Add(receiver, scanner);
                    }
                    System.out.println("The 'add' command requires no arguments.");
                    return null;
                }));

        commands.put("update", new CommandInfo("update",
               "Update the value of a collection element by id", args -> {
                    if (args.length != 1) {
                        System.out.println("One argument 'id' is required");
                        return null;
                    }
                    try {
                        long id = Long.parseLong(args[0]);
                        return new Update(receiver, id, scanner);
                    } catch (NumberFormatException e) {
                        System.out.println("The 'id' argument must be a number");
                        return null;
                    }
                }));

        commands.put("remove_first", new CommandInfo("remove_first",
                "Removes first element of the collection", args -> {
                    if (args.length < 1) {
                        return new RemoveFirst(receiver);
                    }
                    System.out.println("The 'remove_first' command requires no arguments.");
                    return null;
                }));

        commands.put("remove_by_id", new CommandInfo("remove_by_id",
                "Removes the element from collection by 'id'", args -> {
                    if (args.length != 1) {
                        System.out.println("One argument 'id' is required");
                        return null;
                    }
                    try {
                        long id = Long.parseLong(args[0]);
                        return new RemoveById(receiver, id);
                    } catch (NumberFormatException e) {
                        System.out.println("The 'id' argument must be a number");
                        return null;
                    }
                }));

        commands.put("remove_lower", new CommandInfo("remove_lower",
                "Removes all elements from the collection that are less than a specified", args -> {
                    if (args.length < 1) {
                        return new RemoveLower(receiver, scanner);
                    }
                    System.out.println("The 'remove_first' command requires no arguments.");
                    return null;
                }));

        commands.put("clear", new CommandInfo("clear", "Clears the collection",
                args -> {
                    if (args.length < 1) {
                        return new Clear(receiver);
                    }
                    System.out.println("The 'clear' command requires no arguments");
                    return null;
                }));

        commands.put("execute_script", new CommandInfo("execute_script",
                "Executes commands from the file", args -> new ExecuteScript())); //TODO нормально сделать обработку аргументов

        commands.put("save", new CommandInfo("save", "Saves the collection to the file",
                args -> {
                    if (args.length < 1) {
                        return new Save(receiver);
                    }
                    System.out.println("The 'save' command requires no arguments");
                    return null;
                }));

        commands.put("exit", new CommandInfo("exit",
                "Terminates the program without saving the collection", args -> {
                    if (args.length < 1) {
                        return new Exit();
                    }
                    System.out.println("The 'exit' command requires no arguments");
                    return null;
                }));
    }

    public void run() {
        while (true) {
            System.out.print(">> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");
            String commandName = parts[0].toLowerCase();
            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

            CommandInfo info = commands.get(commandName);
            if (info == null) {
                System.out.println("Unsupported command. Enter 'help' for info");
                continue;
            }

            Command command = info.factory().create(commandArgs);
            if (command == null) {
                continue;
            }

            command.execute();

            if (receiver.getHistory().size() == 8) {
                receiver.getHistory().addLast(info.name());
                receiver.getHistory().pollFirst();
            } else {
                receiver.getHistory().addLast(info.name());
            }
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
