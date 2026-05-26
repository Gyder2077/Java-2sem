package main.Commands;

import main.Utils.Command;
import main.Server.MyCollection;
import main.Utils.Response;

import java.io.Serial;

/**
 * Класс команда для отчистки коллекции
 */
public class Clear implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    public Clear() {}

    @Override
    public Response execute(MyCollection myCollection) {
        myCollection.clearCollection();
        return new Response("Clearing the collection...");
    }
}
