package main.Commands;

import main.Model.Ticket;
import main.Utils.*;
import main.Server.MyCollection;

import java.io.Serial;
import java.util.Iterator;
import java.util.Objects;

/**
 * Класс команда для изменения значений полей объекта коллекции по его id
 */
public class Update implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    private final long id;
    private final Ticket newTicket;

    public Update(long id, Ticket newTicket) {
        this.id = id;
        this.newTicket = newTicket;
    }

    @Override
    public Response execute(MyCollection myCollection) {
        if (myCollection.getMyCollection().isEmpty()) {
            return new Response("The collection is empty");
        }
        Iterator<Ticket> iterator = myCollection.getMyCollection().iterator();
        while (iterator.hasNext()) {
            Ticket t = iterator.next();
            if (t.getId() == id) {
                if (Objects.isNull(newTicket)) {
                    return new Response("Unable to update element with such id");
                }
                iterator.remove();
                myCollection.addElement(newTicket);
                return new Response("Element updated successfully");
            }
        }
        return new Response("There is no element with such id in the collection");
    }
}
