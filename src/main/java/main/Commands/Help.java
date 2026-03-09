package main.Commands;

import main.Utils.Command;

public class Help implements Command {
    public Help() {}

    @Override
    public void execute(String[] args) {

    }

    @Override
    public String toString() {
        return "Shows list of commands";
    }
}
