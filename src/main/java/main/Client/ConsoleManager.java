package main.Client;

import main.Commands.*;
import main.Model.Enums.TicketType;
import main.Model.Ticket;
import main.Utils.*;

import java.io.IOException;
import java.util.*;

/**
 * Класс осуществляющий запуск и корректную работу консольного приложения
 */
public class ConsoleManager {
    private Scanner scanner = new Scanner(System.in);
    private final Map<String, CommandInfo> commands = new HashMap<>();
    private final Set<String> executingScripts = new HashSet<>();
    private final ConnectionManager connectionManager;

    /**
     * Создание объекта класса и регистрация всех реализованных команд в базе commands
     */
    public ConsoleManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;

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
                        return new Info();
                    }
                    System.out.println("The 'info' command requires no arguments.");
                    return null;
                }));
        
        commands.put("history", new CommandInfo("history", "Shows last 8 commands used",
                args -> {
                    if (args.length < 1) {
                        return new History();
                    }
                    System.out.println("The 'history' command requires no arguments.");
                    return null;
                }));

        commands.put("show", new CommandInfo("show", "Shows all elements in the collection",
                args -> {
                    if (args.length < 1) {
                        return new Show();
                    }
                    System.out.println("The 'show' command requires no arguments.");
                    return null;
                }));

        commands.put("print_descending", new CommandInfo("print_descending",
                "Shows all elements sorted in the reverse", args -> {
                    if (args.length < 1) {
                        return new PrintDescending();
                    }
                    System.out.println("The 'print_descending' command requires no arguments.");
                    return null;
                }));

        commands.put("filter_contains_name", new CommandInfo("filter_contains_name",
                "Shows elements whose name field value contains the specified substring", args -> {
                    if (args.length != 1) {
                        System.out.println("One argument 'name' is required");
                        return null;
                    } return new FilterContainsName(args[0]);
                }));

        commands.put("filter_less_than_type", new CommandInfo("filter_less_than_type",
                "Shows elements whose type field value if less than the argument", args -> {
                    if (args.length != 1) {
                        System.out.println("One argument 'type' is required");
                        return null;
                    }
                    try {
                        return new FilterLessThanType(TicketType.valueOf(args[0]));
                    } catch (IllegalArgumentException e) {
                        System.out.println("The 'type' argument must be one of the Ticket types");
                        return null;
                    }
                }));

        commands.put("add", new CommandInfo("add",
                "Adds the element with following parameters to the collection", args -> {
                    if (args.length < 1) {
                        Ticket newTicket = new Ticket();
                        InputManager inputManager = InputManager.getInstance();
                        inputManager.setScanner(scanner);
                        newTicket = inputManager.parseObject(newTicket);
                        return new Add(newTicket);
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
                        Ticket newTicket = new Ticket(id);
                        InputManager inputManager = InputManager.getInstance();
                        inputManager.setScanner(scanner);
                        newTicket = inputManager.parseObject(newTicket);
                        return new Update(id, newTicket);
                    } catch (NumberFormatException e) {
                        System.out.println("The 'id' argument must be a number");
                        return null;
                    }
                }));

        commands.put("remove_first", new CommandInfo("remove_first",
                "Removes first element of the collection", args -> {
                    if (args.length < 1) {
                        return new RemoveFirst();
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
                        return new RemoveById(id);
                    } catch (NumberFormatException e) {
                        System.out.println("The 'id' argument must be a number");
                        return null;
                    }
                }));

        commands.put("remove_lower", new CommandInfo("remove_lower",
                "Removes all elements from the collection that are less than a specified", args -> {
                    if (args.length < 1) {
                        Ticket ticket = new Ticket(0);
                        InputManager inputManager = InputManager.getInstance();
                        inputManager.setScanner(scanner);
                        ticket = inputManager.parseObject(ticket);
                        return new RemoveLower(ticket);
                    }
                    System.out.println("The 'remove_first' command requires no arguments.");
                    return null;
                }));

        commands.put("clear", new CommandInfo("clear", "Clears the collection",
                args -> {
                    if (args.length < 1) {
                        return new Clear();
                    }
                    System.out.println("The 'clear' command requires no arguments");
                    return null;
                }));

//        commands.put("execute_script", new CommandInfo("execute_script",
//                    "Executes commands from the file, from environment variable 'SCRIPT_FILE'", args -> {
//                    if (args.length == 1) {
//                        return new ExecuteScript(this, args[0], executingScripts);
//                    }
//                    System.out.println("One argument 'filename' is required");
//                    return null;
//                }));
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Непосредственный запуск приложения
     */
    public void run() {
        System.out.println("Client is running. Enter 'help' for info");
        while (true) {
            System.out.print(">> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");
            String commandName = parts[0].toLowerCase();
            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);


            if (commandName.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the program...");
                break;
            }

            CommandInfo info = commands.get(commandName);
            if (info == null) {
                System.out.println("Unsupported command. Enter 'help' for info");
                continue;
            }

            Command command = info.factory().create(commandArgs);
            if (command == null) {
                continue;
            }

            try {
                Response response = connectionManager.sendCommand(command);
                System.out.println(response.message());
            } catch (IOException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

//    /**
//     * Обработка поступающих из консоли/скрипта команд
//     */
//    public boolean runCommand(String input) {
//        String[] parts = input.split("\\s+");
//        String commandName = parts[0].toLowerCase();
//        String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);
//
//        CommandInfo info = commands.get(commandName);
//        if (info == null) {
//            System.out.println("Unsupported command. Enter 'help' for info");
//            return false;
//        }
//
//        Command command = info.factory().create(commandArgs);
//        if (command == null) {
//            return false;
//        }
//
//        System.out.println("Executing command: " + info.name());
//        System.out.println();
//        command.execute(receiver);
//
//
//        if (receiver.getHistory().size() == 8) {
//            receiver.getHistory().addLast(info.name());
//            receiver.getHistory().pollFirst();
//        } else {
//            receiver.getHistory().addLast(info.name());
//        }
//        System.out.println();
//        return true;
//    }
}
