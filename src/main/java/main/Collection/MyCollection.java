package main.Collection;

import main.Given.Ticket;

import java.util.*;

public class MyCollection {
    private ArrayDeque<Ticket> myCollection = new ArrayDeque<>();
    private static final MyCollection Instance = new MyCollection();

    private MyCollection() {}

    public static MyCollection getInstance() {
        return Instance;
    }

    public ArrayDeque<Ticket> getMyCollection() {
        return myCollection;
    }

    public void setMyCollection(ArrayDeque<Ticket> myCollection) {
        this.myCollection = myCollection;
    }
}
