package main.Commands;

import main.Given.Ticket;
import main.Utils.Command;
import main.Utils.MyCollection;

public class RemoveFirst implements Command {
    private final MyCollection myCollection;

    public RemoveFirst(MyCollection myCollection) {this.myCollection = myCollection;}

    @Override
    public void execute(String[] args) {
        Ticket removed = myCollection.getMyCollection().pollFirst();
        if (removed == null) {
            throw new IllegalArgumentException("There is no such element in the collection");
        }

    }

    @Override
    public String toString() {
        return "Removes first element of the collection";
    }
}
