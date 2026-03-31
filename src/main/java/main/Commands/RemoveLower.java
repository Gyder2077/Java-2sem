package main.Commands;

import main.Given.Ticket;
import main.Utils.*;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

public class RemoveLower implements Command {
    private final MyCollection myCollection;
    private final Scanner scanner;

    public RemoveLower(MyCollection myCollection, Scanner scanner) {
        this.myCollection = myCollection;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        if (myCollection.getMyCollection().isEmpty()) {
            System.out.println("The collection is empty");
            return;
        }
        InputManager inputManager = InputManager.getInstance();
        inputManager.setScanner(scanner);
        Ticket removeLower = inputManager.parseObject(new Ticket(0));
        if (Objects.isNull(removeLower)) {
            return;
        }
        long counter = 0;
        Iterator<Ticket> iterator = myCollection.getMyCollection().iterator();
        while (iterator.hasNext()) {
            Ticket t = iterator.next();
            if (t.compareTo(removeLower) < 0) {
                counter++;
                iterator.remove();
            }
        }
        System.out.printf("Total of %d element were removed from the collection%n", counter);
    }
}
