package main.Commands;

import main.Utils.Command;

import java.util.ArrayDeque;

public class History implements Command {
    private final ArrayDeque<String> history;

    public History(ArrayDeque<String> history) {this.history = history;}

    @Override
    public void execute() {
        if (history.isEmpty()) {
            System.out.println("You have never used any commands");
            return;
        }
        int i = 0;
        System.out.println("Last used commands:");
        for (String command : history) {
            System.out.printf("%d - %s%n", ++i, command);
        }
    }
}
