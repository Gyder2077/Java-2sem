package main;

import main.Server.FileManager;
import main.Server.MyCollection;

import java.util.Objects;

/**
 * Запускаемый класс
 */
public class Main {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        MyCollection collection = fileManager.parseXML();
        if (Objects.isNull(collection)) System.out.println("Something went wrong, please try again");
//        Invoker invoker = new Invoker(fileManager);
//        invoker.run();
    }
}
