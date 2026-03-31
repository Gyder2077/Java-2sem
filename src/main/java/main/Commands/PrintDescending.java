package main.Commands;

import main.Utils.Command;
import main.Utils.MyCollection;

/**
 * Класс команда для вывода всех объектов коллекции в обратном порядке
 */
public class PrintDescending implements Command {
    private final MyCollection myCollection;

    public PrintDescending(MyCollection myCollection) {this.myCollection = myCollection;}

    @Override
    public void execute() {
        if (myCollection.getMyCollection().isEmpty()) {
            System.out.println("The collection is empty");
            return;
        }
        myCollection.showAll(true);
    }
}
