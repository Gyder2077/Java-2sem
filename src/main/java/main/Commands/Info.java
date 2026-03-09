package main.Commands;

import main.Utils.Command;
import main.Utils.MyCollection;

public class Info implements Command {
    private final MyCollection myCollection;

    public Info(MyCollection myCollection) {this.myCollection = myCollection;}

    @Override
    public void execute(String[] args){

    }
}
