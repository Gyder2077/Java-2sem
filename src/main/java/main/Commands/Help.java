package main.Commands;

import main.Server.MyCollection;
import main.Utils.*;

import java.io.Serial;
import java.util.*;

/**
 * Класс команда для вывода справки о существующих командах
 */
public class Help implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, CommandInfo> commands;

    public Help(Map<String, CommandInfo> commands) {this.commands = commands;}

    @Override
    public Response execute(MyCollection myCollection) {
        StringBuilder response = new StringBuilder("Available commands:\n");
        commands.values().stream()
                .sorted(Comparator.comparing(CommandInfo::name))
                .forEach(info -> response.append(
                        String.format("%-21s - %s%n", info.name(), info.description())));
        return new Response(response.toString());
    }
}
