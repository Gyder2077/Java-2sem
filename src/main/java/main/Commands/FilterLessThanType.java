package main.Commands;

import main.Given.Enums.TicketType;
import main.Utils.*;

public class FilterLessThanType implements Command {
    private final TicketType type;
    private final MyCollection myCollection;

    public FilterLessThanType(MyCollection myCollection, TicketType type) {
        this.myCollection = myCollection;
        this.type = type;
    }

    @Override
    public void execute() {

    }
}
