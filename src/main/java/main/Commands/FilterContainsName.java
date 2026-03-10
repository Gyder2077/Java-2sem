package main.Commands;

import main.Utils.Command;
import main.Utils.MyCollection;

public class FilterContainsName implements Command {
    private final String name;
    private final MyCollection myCollection;

    public FilterContainsName(MyCollection myCollection, String name) {
        this.name = name;
        this.myCollection = myCollection;
    }

    @Override
    public void execute() {

    }
}
