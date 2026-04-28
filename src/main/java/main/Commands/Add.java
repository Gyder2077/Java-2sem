package main.Commands;

import main.Model.Ticket;
import main.Utils.*;
import main.Server.MyCollection;

import java.io.Serial;
import java.util.Objects;

/**
 * Класс команда для добавления объекта в коллекцию
 */
public class Add implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Ticket newTicket;

    public Add(Ticket newTicket) {
        this.newTicket = newTicket;
    }

    @Override
    public Response execute(MyCollection myCollection) {
        if (!Objects.isNull(newTicket)) {
            newTicket.idSetter(myCollection.getNextId());
            myCollection.setNextId(newTicket.getId() + 1);
            myCollection.addElement(newTicket);
            return new Response(String.format("Element with id = %d added successfully!%n", newTicket.getId()));
        }
        return new Response("Unexpected ERROR, try again later");
    }
}
