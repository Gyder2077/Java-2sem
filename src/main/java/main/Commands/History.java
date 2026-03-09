package main.Commands;

import main.Utils.Command;

public class History implements Command {
    public History() {}

    @Override
    public void execute(String[] args) {

    }

    @Override
    public String toString() {
        return "Shows last 8 commands used";
    }
}
