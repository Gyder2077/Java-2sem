package main.Commands;

import main.Utils.Command;
import main.Server.MyCollection;
import main.Utils.Response;

import java.io.Serial;

/**
 * Класс команда для вывода информации о коллекции
 */
public class Info implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    public Info() {}

    @Override
    public Response execute(MyCollection myCollection) {
        return new Response(
                String.format("Collection type: %s%nCollection size: %d%nCreation date: %s%n",
                        myCollection.getTickets().getClass().getName(),
                        myCollection.getTickets().size(), myCollection.getCreationDate()));
    }
}
