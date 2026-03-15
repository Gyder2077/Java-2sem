package main.Commands;

import main.Utils.Command;
import main.Utils.MyCollection;

public class Show implements Command {
    private final MyCollection myCollection;

    public Show(MyCollection myCollection) {this.myCollection = myCollection;}

    @Override
    public void execute() {
        if (myCollection.getMyCollection().isEmpty()) {
            System.out.println("The collection is empty");
            return;
        }
        myCollection.showAll(false);
    }
}
