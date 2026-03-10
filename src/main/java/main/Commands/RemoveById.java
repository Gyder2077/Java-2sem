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

    }
}
