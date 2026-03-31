package main.Commands;

import main.Utils.*;

/**
 * Класс команда для сохранения коллекции и некоторых других данных в файл Collection.xml
 */
public class Save implements Command {
    private final MyCollection myCollection;
    private final FileManager fileManager;

    public Save(MyCollection myCollection, FileManager fileManager) {
        this.myCollection = myCollection;
        this.fileManager = fileManager;
    }

    @Override
    public void execute() {
        if (!fileManager.writeXML(myCollection)) System.out.println("Unexpected EROR, please try again");
    }
}
