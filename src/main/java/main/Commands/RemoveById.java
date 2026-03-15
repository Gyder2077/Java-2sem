package main.Commands;

import main.Utils.*;

public class RemoveById implements Command {
    private final MyCollection myCollection;
    private final long id;

    public RemoveById(MyCollection myCollection, long id) {
        this.myCollection = myCollection;
        this.id = id;
    }

    @Override
    public void execute() {
        if (myCollection.getMyCollection().isEmpty()) {
            System.out.println("The collection is empty");
            return;
        }
        try {
            myCollection.delElement(id);
        } catch (IllegalArgumentException e) {
            System.out.println("The collection does not contain element with such id");
        }
    }
}
