package main.Commands;

import main.Given.Ticket;
import main.Utils.*;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

/**
 * Класс команда для изменения значений полей объекта коллекции по его id
 */
public class Update implements Command {
    private final MyCollection myCollection;
    private final long id;
    private final Scanner scanner;

    public Update(MyCollection myCollection, long id, Scanner scanner) {
        this.myCollection = myCollection;
        this.id = id;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        if (myCollection.getMyCollection().isEmpty()) {
            System.out.println("The collection is empty");
            return;
        }
        Ticket toUpdate;
        Iterator<Ticket> iterator = myCollection.getMyCollection().iterator();
        while (iterator.hasNext()) {
            Ticket t = iterator.next();
            if (t.getId() == id) {
                InputManager inputManager = InputManager.getInstance();
                inputManager.setScanner(scanner);
                toUpdate = inputManager.parseObject(t);
                if (Objects.isNull(toUpdate)) {
                    return;
                }
                iterator.remove();
                myCollection.addElement(toUpdate);
                break;
            }
        }
    }
}
