package main.Utils;

import main.Given.Enums.TicketType;
import main.Given.Ticket;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MyCollection {
    private ArrayDeque<Ticket> myCollection;
    private final ArrayDeque<String> history = new ArrayDeque<>();
    private final ZonedDateTime dateTime;
    private long nextId = 0;

    public MyCollection() {
        myCollection = new ArrayDeque<>();
        dateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public ArrayDeque<String> getHistory() {return history;}

    public ArrayDeque<Ticket> getMyCollection() {
        return myCollection;
    }

    public void setMyCollection(ArrayDeque<Ticket> myCollection) {
        this.myCollection = myCollection;
    }

    public long getNextId() {
        nextId += 1;
        return nextId;
    }

    public void clearing() {
        myCollection.clear();
    }

    public void addElement(Ticket ticket) {
        myCollection.add(ticket);
        Ticket[] array = myCollection.toArray(new Ticket[0]);
        Arrays.sort(array);
        myCollection.clear();
        Collections.addAll(myCollection, array);
    }

    public void delElement(long id) {
        Iterator<Ticket> iterator = myCollection.iterator();
        while (iterator.hasNext()) {
            Ticket t = iterator.next();
            if (t.getId() == id) {
                iterator.remove();
                return;
            }
        }
        throw new IllegalArgumentException("No element with such ID");
    }

    public void showAll(boolean descending) {
        System.out.print("All elements from the collection");
        Iterator<Ticket> iterator;
        if (descending) {
            System.out.println(" in reverse:");
            iterator = myCollection.descendingIterator();
        } else {
            System.out.println(":");
            iterator = myCollection.iterator();
        }
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    public void filtered(String name) {
        boolean found = false;
        System.out.println("Filtered elements:");
        for (Ticket ticket : myCollection) {
            if (ticket.getName().contains(name)) {
                System.out.println(ticket);
                found = true;
            }
        }
        if (!found) System.out.println("No elements with appropriate name were found");
    }

    public void filtered(TicketType type) {
        boolean found = false;
        System.out.println("Filtered elements:");
        for (Ticket ticket : myCollection) {
            if (ticket.getType().ordinal() <= type.ordinal()) {
                System.out.println(ticket);
                found = true;
            }
        }
        if (!found) System.out.println("No elements with appropriate type were found");
    }
}
