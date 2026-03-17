package main;

import main.Utils.*;

public class Main {
    public static void main(String[] args) {
        MyCollection collection = new MyCollection();
        FileManager fileManager = new FileManager();
        if (!fileManager.parseXML(collection)) System.out.println("Something went wrong, please try again");
        Invoker invoker = new Invoker(collection, fileManager);
        invoker.run();
    }
}
