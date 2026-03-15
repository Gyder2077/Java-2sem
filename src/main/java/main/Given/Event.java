package main.Given;
import main.Given.Enums.*;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Event {
    private final long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.time.ZonedDateTime date; //Поле не может быть null
    private EventType eventType; //Поле не может быть null

    public Event(long id) {
        this.id = id;
    }

    public void setName(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("Field 'name' can not be NULL");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDate(java.time.ZonedDateTime date) {
        if (Objects.isNull(date)) {
            throw new IllegalArgumentException("Field 'date' can not be NULL");
        }
        this.date = date;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setEventType(EventType eventType) {
        if (Objects.isNull(eventType)) {
            throw new IllegalArgumentException("Field 'eventType' can not be NULL");
        }
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date, eventType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {return false;}
        Event that = (Event) obj;
        return super.equals(obj) && Objects.equals(that.name, name) && Objects.equals(that.id, id)
                && Objects.equals(that.date, date) && Objects.equals(that.eventType, eventType);
    }

    @Override
    public String toString() {
        return String.format("Event {id = %d, name = %s, date = %s, eventType = %s}",
                id, name, date, eventType);
    }
}