package main.Given;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import main.Given.Enums.TicketType;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Ticket implements Comparable<Ticket> {
    @JacksonXmlProperty(isAttribute = true)
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @JacksonXmlProperty(isAttribute = true)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @JacksonXmlProperty(isAttribute = true)
    private Coordinates coordinates; //Поле не может быть null

    @JacksonXmlProperty(isAttribute = true)
    private java.time.ZonedDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @JacksonXmlProperty(isAttribute = true)
    private int price; //Значение поля должно быть больше 0

    @JacksonXmlProperty(isAttribute = true)
    private boolean refundable;

    @JacksonXmlProperty(isAttribute = true)
    private TicketType type; //Поле может быть null

    @JacksonXmlProperty(isAttribute = true)
    private Event event; //Поле не может быть null

    public Ticket() {}

    public Ticket(long id) {
        this.id = id;
        creationDate = ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public Ticket(long id, String name, Coordinates coordinates, ZonedDateTime dateTime, int price,
                  boolean refundable, TicketType ticketType, Event event) {
        this.id = id;
        setName(name);
        setCoordinates(coordinates);
        this.creationDate = dateTime;
        setPrice(price);
        setRefundable(refundable);
        setType(ticketType);
        setEvent(event);
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
        return String.format("""
                        {
                            id='%d',
                            name='%s',
                            coordinates='%s',
                            creationDate='%s',
                            price='%d',
                            refundable='%s',
                            type='%s',
                            event='%s'
                        }""",
                id, name, coordinates, creationDate, price, refundable, type, event);
    }

    @Override
    public int compareTo(Ticket other) {
        if (Integer.compare(this.price, other.price) +
                Long.compare(this.id, other.id) +
                CharSequence.compare(this.name, other.name) <= -1) return -1;
        return 1;
    }
}