package main.Given;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import main.Given.Enums.*;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Подкласс, хранящийся в коллекции
 */
public class Event {
    @JacksonXmlProperty(isAttribute = true)
    private long eventId; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @JacksonXmlProperty(isAttribute = true)
    private String eventName; //Поле не может быть null, Строка не может быть пустой

    @JacksonXmlProperty(isAttribute = true)
    private java.time.ZonedDateTime date; //Поле не может быть null

    @JacksonXmlProperty(isAttribute = true)
    private EventType eventType; //Поле не может быть null

    public Event() {}

    public Event(long eventId) {
        this.eventId = eventId;
    }

    public Event(long eventId, String eventName, ZonedDateTime date, EventType eventType) {
        this.eventId = eventId;
        setEventName(eventName);
        setDate(date);
        setEventType(eventType);
    }

    public long getEventId() {return eventId;}

    public void setEventName(String eventName) {
        if (Objects.isNull(eventName) || eventName.isEmpty()) {
            throw new IllegalArgumentException("Field 'eventName' can not be NULL");
        }
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
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
        return Objects.hash(eventId, eventName, date, eventType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {return false;}
        Event that = (Event) obj;
        return super.equals(obj) && Objects.equals(that.eventName, eventName) && Objects.equals(that.eventId, eventId)
                && Objects.equals(that.date, date) && Objects.equals(that.eventType, eventType);
    }

    @Override
    public String toString() {
        return String.format("Event {eventId = %d, eventName = %s, date = %s, eventType = %s}",
                eventId, eventName, date, eventType);
    }
}