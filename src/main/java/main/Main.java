package main;

import main.Utils.Invoker;
import main.Utils.MyCollection;

public class Main {
    public static void main(String[] args) {
        MyCollection collection = new MyCollection();
        Invoker invoker = new Invoker(collection);
        invoker.run();
    }
}
