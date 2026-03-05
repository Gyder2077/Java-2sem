package main.Commands;

import main.Given.Ticket;
import main.Utils.MyCollection;

public class RemoveFirst implements Command {
    public RemoveFirst() {}

    @Override
    public void execute(String[] args) {
        Ticket removed = MyCollection.Instance.getMyCollection().pollFirst();
        if (removed == null) {
            throw new IllegalArgumentException("There is no such element in the collection");
        }

    }

    @Override
    public String toString() {
        return "Removes first element of the collection";
    }
}
