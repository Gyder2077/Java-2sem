package main.Commands;

import main.Utils.Command;

import java.util.ArrayDeque;

public class History implements Command {
    private final ArrayDeque<String> history;

    public History(ArrayDeque<String> history) {this.history = history;}

    @Override
    public void execute() {

    }
}
