package main;

import main.Utils.*;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        MyCollection collection = fileManager.parseXML();
        if (Objects.isNull(collection)) System.out.println("Something went wrong, please try again");
        Invoker invoker = new Invoker(collection, fileManager);
        invoker.run();
    }
}
