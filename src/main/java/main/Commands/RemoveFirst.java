package main.Commands;

import main.Model.Ticket;
import main.Utils.Command;
import main.Server.MyCollection;
import main.Utils.Response;

import java.io.Serial;

/**
 * Класс команда для удаления первого элемента коллекции
 */
public class RemoveFirst implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    public RemoveFirst() {}

    @Override
    public Response execute(MyCollection myCollection) {
        Ticket removed = myCollection.getMyCollection().pollFirst();
        if (removed == null) {
            return new Response("The collection is empty");
        }
        return new Response("The element is removed successfully");
    }
}
