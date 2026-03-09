package main.Commands;

import main.Utils.Command;

public class Exit implements Command {
    public Exit() {}

    @Override
    public void execute(String[] args) {
        System.out.println("Exiting the program...");
        System.exit(0);
    }

    @Override
    public String toString() {
        return "Terminates the program without saving the collection";
    }
}
