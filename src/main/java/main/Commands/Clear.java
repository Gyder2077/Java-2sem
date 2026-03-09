package main.Commands;

import main.Utils.Command;
import main.Utils.MyCollection;

public class Clear implements Command {
    private final MyCollection myCollection;

    public Clear(MyCollection myCollection) {this.myCollection = myCollection;}

    @Override
    public void execute(String[] args) {
        System.out.println("Clearing the collection...");
        myCollection.clearing();
    }
}
