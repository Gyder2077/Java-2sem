package main.Commands;

import main.Model.Ticket;
import main.Utils.*;
import main.Server.MyCollection;

import java.io.Serial;
import java.util.Iterator;
import java.util.Objects;

/**
 * Класс команда для удаления всех элементов коллекции, которые меньше заданного
 */
public class RemoveLower implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Ticket ticket;

    public RemoveLower(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public Response execute(MyCollection myCollection) {
        if (myCollection.getTickets().isEmpty()) {
            return new Response("The collection is empty");
        }
        if (Objects.isNull(ticket)) {
            return new Response("Unexpected ERROR, try again later!");
        }
        long counter = 0;
        Iterator<Ticket> iterator = myCollection.getTickets().iterator();
        while (iterator.hasNext()) {
            Ticket t = iterator.next();
            if (t.compareTo(ticket) < 0) {
                counter++;
                iterator.remove();
            }
        }
        return new Response(String.format("Total of %d element were removed from the collection%n", counter));
    }
}
