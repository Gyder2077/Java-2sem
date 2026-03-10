package main.Commands;

import main.Utils.*;

public class Update implements Command {
    private final MyCollection myCollection;
    private final long id;

    public Update(MyCollection myCollection, long id) {
        this.myCollection = myCollection;
        this.id = id;
    }

    @Override
    public void execute() {

    }
}
