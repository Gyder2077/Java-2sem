package main.Commands;

import main.Model.Enums.TicketType;
import main.Utils.Command;
import main.Server.MyCollection;
import main.Utils.Response;

import java.io.Serial;

/**
 * Класс команда для вывода отфильтрованных по полю type объектов
 */
public class FilterLessThanType implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    private final TicketType type;

    public FilterLessThanType(TicketType type) {
        this.type = type;
    }

    @Override
    public Response execute(MyCollection myCollection) {
        if (myCollection.getMyCollection().isEmpty()) {
            return new Response("The collection is empty");
        }
        return myCollection.filtered(type);
    }
}
