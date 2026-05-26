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
    public Response execute(MyCollection collection) {
        synchronized (collection.getTickets()) {
            if (collection.getTickets().isEmpty()) {
                return new Response("Collection is empty", false);
            }
            Ticket first = collection.getTickets().iterator().next();
            collection.delElement(first.getId());
            return new Response("First object deleted successfully");
        }
    }
}
