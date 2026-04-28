package main.Commands;

import main.Utils.Command;
import main.Server.MyCollection;
import main.Utils.Response;

import java.io.Serial;

/**
 * Класс команда для удаления элемента по его id
 */
public class RemoveById implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    private final long id;

    public RemoveById(long id) {
        this.id = id;
    }

    @Override
    public Response execute(MyCollection myCollection) {
        if (myCollection.getMyCollection().isEmpty()) {
            return new Response("The collection is empty");
        }
        try {
            myCollection.delElement(id);
            return new Response("The element is removed successfully");
        } catch (IllegalArgumentException e) {
            return new Response("The collection does not contain element with such id");
        }
    }
}
