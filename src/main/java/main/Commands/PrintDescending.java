package main.Commands;

import main.Utils.Command;
import main.Server.MyCollection;
import main.Utils.Response;

import java.io.Serial;

/**
 * Класс команда для вывода всех объектов коллекции в обратном порядке
 */
public class PrintDescending implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    public PrintDescending() {}

    @Override
    public Response execute(MyCollection myCollection) {
        if (myCollection.getMyCollection().isEmpty()) {
            return new Response("The collection is empty");
        }
        return myCollection.showAll(true);
    }
}
