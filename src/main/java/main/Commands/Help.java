package main.Commands;

import main.Utils.*;
import java.util.*;

public class Help implements Command {
    private final Map<String, CommandInfo> commands;

    public Help(Map<String, CommandInfo> commands) {this.commands = commands;}

    @Override
    public void execute() {
        System.out.println("Available commands:");
        for (Object obj : commands.values().stream().sorted(Comparator.comparing(CommandInfo::name)).toArray()) {
            CommandInfo info = (CommandInfo) obj;
            System.out.printf("%-21s - %s%n", info.name(), info.description());
        }
    }
}
