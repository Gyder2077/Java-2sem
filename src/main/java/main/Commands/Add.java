package main.Commands;

import main.Utils.Command;
import main.Utils.MyCollection;

public class Add implements Command {
    private final MyCollection myCollection;

    public Add(MyCollection myCollection) {this.myCollection = myCollection;}

    @Override
    public void execute(String[] args) {

    }
}
