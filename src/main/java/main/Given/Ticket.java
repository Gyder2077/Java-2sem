package main.Given;

import main.Given.Enums.TicketType;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Ticket implements Comparable<Ticket> {
    private final long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private final java.time.ZonedDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int price; //Значение поля должно быть больше 0
    private boolean refundable;
    private TicketType type; //Поле может быть null
    private Event event; //Поле не может быть null

    public Ticket(long id) {
        this.id = id;
        creationDate = ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public long getId() {return id;}

    public void setName(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("Field 'name' can not be NULL");
        }
        this.name = name;
    }

    public String getName() {return name;}

    public void setCoordinates(Coordinates coordinates) {
        if (Objects.isNull(coordinates)) {
            throw new IllegalArgumentException("Field 'coordinates' can not be NULL");
        }
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {return coordinates;}

    public void setPrice(int price) {
        if (price > 0) {
            this.price = price;
            return;
        }
        throw new IllegalArgumentException("Field 'price' must be positive");
    }

    public int getPrice() {return price;}

    public void setRefundable(boolean refundable) {this.refundable = refundable;}

    public boolean getRefundable() {return refundable;}

    public void setType(TicketType type) {this.type = type;}

    public TicketType getType() {return type;}

    public void setEvent(Event event) {
        if (Objects.isNull(event)) {
            throw new IllegalArgumentException("Field 'event' can not be NULL");
        }
        this.event = event;
    }

    public Event getEvent() {return event;}

    public ZonedDateTime getCreationDate() {return creationDate;}


    @Override
    public int hashCode() {return Objects.hash(id, name, coordinates, creationDate, price, refundable, type, event);}

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {return false;}
        Ticket that = (Ticket) obj;
        return super.equals(obj) && Objects.equals(that.name, name) && Objects.equals(that.id, id)
                && Objects.equals(that.coordinates, coordinates) && Objects.equals(that.creationDate, creationDate)
                && Objects.equals(that.price, price) && Objects.equals(that.refundable, refundable)
                && Objects.equals(that.type, type) && Objects.equals(that.event, event);
    }

    @Override
    public String toString() {
        return String.format("Ticket {id='%d', name='%s', coordinates='%s', creationDate='%s'," +
                        " price='%d', refundable='%s', type='%s', event='%s'}",
                id, name, coordinates, creationDate, price, refundable, type, event);
    }

    @Override
    public int compareTo(Ticket other) {
        return Integer.compare(this.price, other.price) + Integer.compare(this.type.ordinal(), other.type.ordinal());
    }
}