package main.Commands;

import main.Given.Ticket;
import main.Utils.*;

import java.io.BufferedReader;
import java.util.Objects;
import java.util.Scanner;

public class Add implements Command {
    private final MyCollection myCollection;
    private final Scanner scanner;

    public Add(MyCollection myCollection, Scanner scanner) {
        this.myCollection = myCollection;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        Ticket newTicket = new Ticket(myCollection.getNextId());
        myCollection.setNextId(newTicket.getId() + 1);
        newTicket = new InputManager().parseObject(newTicket, scanner);
        if (!Objects.isNull(newTicket)) {
            myCollection.addElement(newTicket);
            System.out.printf("Element with id = %d added successfully!%n", newTicket.getId());
        }
    }

    @Override
    public boolean executeByScript(BufferedReader reader, FileManager fileManager) {
        Ticket newTicket = new Ticket(myCollection.getNextId());
        myCollection.setNextId(newTicket.getId() + 1);
        newTicket = fileManager.readObject(newTicket, reader);
        if (!Objects.isNull(newTicket)) {
            myCollection.addElement(newTicket);
            System.out.printf("Element with id = %d added successfully!%n", newTicket.getId());
            return true;
        }
        return false;
    }
}
