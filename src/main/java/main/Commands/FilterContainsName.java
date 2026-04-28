package main.Commands;

import main.Utils.Command;
import main.Server.MyCollection;
import main.Utils.Response;

import java.io.Serial;

/**
 * Класс команда для вывода отфильтрованных по полю name объектов
 */
public class FilterContainsName implements Command {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;

    public FilterContainsName(String name) {
        this.name = name;
    }

    @Override
    public Response execute(MyCollection myCollection) {
        if (myCollection.getMyCollection().isEmpty()) {
            return new Response("The collection is empty");
        }
        return myCollection.filtered(name);
    }
}
