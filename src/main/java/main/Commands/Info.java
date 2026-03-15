package main.Commands;

import main.Utils.*;

public class Info implements Command {
    private final MyCollection coll;

    public Info(MyCollection coll) {this.coll = coll;}

    @Override
    public void execute() {
        System.out.printf("Collection type: %s%nCollection size: %d%nCreation date: %s%n",
                coll.getMyCollection().getClass().getName(), coll.getMyCollection().size(), coll.getDateTime());
    }
}
