package main.Commands;

import main.Server.MyCollection;
import main.Utils.*;

import java.io.Serial;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс команда для вывода последних 8 использованных команд
 */
public class History implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    public History() {}


    @Override
    public Response execute(MyCollection myCollection) {
        ArrayDeque<String> history = myCollection.getHistory();
        if (history.isEmpty()) {
            return new Response("You have never used any commands");
        }
        StringBuilder response = new StringBuilder();
        response.append("Last used commands:\n");
        AtomicInteger i = new AtomicInteger(1);
        history.stream().forEach(command ->
                response.append(String.format("%d - %s%n", i.getAndIncrement(), command))
        );
        return new Response(response.toString());
    }
}
