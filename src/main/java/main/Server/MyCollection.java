package main.Server;

import main.Model.Enums.TicketType;
import main.Model.Ticket;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import main.Utils.Response;

/**
 * Класс коллекция объектов
 */
@JacksonXmlRootElement(localName = "collection")
public class MyCollection {
    @JsonDeserialize(as = ArrayDeque.class)
    @JacksonXmlProperty(localName = "ticket")
    @JacksonXmlElementWrapper(localName = "tickets")
    private ArrayDeque<Ticket> myCollection = new ArrayDeque<>();

    @JsonDeserialize(as = ArrayDeque.class)
    @JacksonXmlProperty(localName = "command")
    @JacksonXmlElementWrapper(localName = "history")
    private ArrayDeque<String> history = new ArrayDeque<>();

    @JacksonXmlProperty(localName = "creationDate")
    private ZonedDateTime creationDate;

    @JacksonXmlProperty(localName = "nextId")
    private long nextId;

    public MyCollection() {
        creationDate = ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public void setCreationDate(ZonedDateTime creationDate) {this.creationDate = creationDate;}

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setHistory(ArrayDeque<String> history) {this.history = history;}

    public ArrayDeque<String> getHistory() {return history;}

    public ArrayDeque<Ticket> getMyCollection() {
        return myCollection;
    }

    public void setMyCollection(ArrayDeque<Ticket> myCollection) {
        this.myCollection = myCollection;
    }

    public void setNextId(long nextId) {this.nextId = nextId;}

    public long getNextId() {
        return nextId;
    }

    public void clearing() {
        myCollection.clear();
    }

    public void addElement(Ticket ticket) {
        myCollection.add(ticket);
        myCollection = myCollection.stream()
                .sorted().collect(Collectors.toCollection(ArrayDeque::new));
    }

    public void delElement(long id) {
        Ticket toRemove = myCollection.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No element with such ID"));
        myCollection.remove(toRemove);
    }

    public Response showAll(boolean descending) {
        String header = "All elements from the collection"
                + (descending ? " in reverse:\n" : ":\n");

        String body = (descending ? myCollection.stream()
                .sorted(Comparator.reverseOrder()) : myCollection.stream())
                .map(ticket -> ticket.toString() + "\n")
                .collect(Collectors.joining());

        return new Response(header + body);
    }

    public Response filtered(String name) {
        StringBuilder response = new StringBuilder("Filtered elements:\n");
        var filteredList = myCollection.stream()
                .filter(ticket -> ticket.getName().contains(name))
                .toList();
        if (filteredList.isEmpty()) {
            return new Response("No elements with appropriate name were found");
        } else {
            filteredList.forEach(response::append);
            return new Response(response.toString());
        }
    }

    public Response filtered(TicketType type) {
        StringBuilder response = new StringBuilder("Filtered elements:\n");
        var filteredList = myCollection.stream()
                .filter(ticket -> ticket.getType().ordinal() <= type.ordinal())
                .toList();
        if (filteredList.isEmpty()) {
            return new Response("No elements with appropriate name were found");
        } else {
            filteredList.forEach(response::append);
            return new Response(response.toString());
        }
    }
}
