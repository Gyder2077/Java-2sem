package main.Utils;

import main.Given.Ticket;
import java.util.*;

public class MyCollection {
    private static ArrayDeque<Ticket> myCollection;
    private static long nextId = 0;
    public static final MyCollection Instance = new MyCollection();

    private MyCollection() {myCollection = new ArrayDeque<>();}

    public ArrayDeque<Ticket> getMyCollection() {
        return myCollection;
    }

    public static void setMyCollection(ArrayDeque<Ticket> myCollection) {
        MyCollection.myCollection = myCollection;
    }

    public static long getNextId() {
        nextId += 1;
        return nextId;
    }

    public void addElement(Ticket ticket) {
        myCollection.add(ticket);
        Ticket[] array = myCollection.toArray(new Ticket[0]);
        Arrays.sort(array);
        myCollection.clear();
        Collections.addAll(myCollection, array);
    }

    public boolean delElement(long id) {
        Iterator<Ticket> iterator = myCollection.iterator();
        while (iterator.hasNext()) {
            Ticket t = iterator.next();
            if (t.getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

     public String showElement(long id) {
         for (Ticket t : myCollection) {
             if (t.getId() == id) {
                 return t.toString();
             }
         }
         throw new IllegalArgumentException("No element with such ID");
     }
}
