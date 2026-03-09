package main.Commands;

import main.Utils.Command;

public class RemoveById implements Command {
    public RemoveById() {}

    @Override
    public void execute(String[] args) {

    }

    @Override
    public String toString() {
        return "Removes the element from collection by ID";
    }
}
