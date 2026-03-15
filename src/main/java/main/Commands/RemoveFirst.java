package main.Commands;

import main.Given.Ticket;
import main.Utils.Command;
import main.Utils.MyCollection;

public class RemoveFirst implements Command {
    private final MyCollection myCollection;

    public RemoveFirst(MyCollection myCollection) {this.myCollection = myCollection;}

    @Override
    public void execute() {
        Ticket removed = myCollection.getMyCollection().pollFirst();
        if (removed == null) {
            System.out.println("The collection is empty");
        }
    }
}
