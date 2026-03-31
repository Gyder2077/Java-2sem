package main.Given;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.Objects;

/**
 * Подкласс, хранящийся в коллекции
 */
public class Coordinates {
    @JacksonXmlProperty(isAttribute = true)
    private Float coordinateX; //Поле не может быть null

    @JacksonXmlProperty(isAttribute = true)
    private Double coordinateY; //Значение поля должно быть больше -231, Поле не может быть null

    public Coordinates() {}

    public Coordinates(Float x, Double y) {
        setCoordinateX(x);
        setCoordinateY(y);
    }

    public Float getCoordinateX() {return coordinateX;}

    public Double getCoordinateY() {return coordinateY;}

    public void setCoordinateX(Float coordinateX) {
        if (!(coordinateX == null)) {
            this.coordinateX = coordinateX;
            return;
        }
        throw new IllegalArgumentException("X Coordinates should not be null");
    }

    public void setCoordinateY(Double coordinateY) {
        if (!(coordinateY == null || coordinateY <= -231.0)) {
        this.coordinateY = coordinateY;
        return;
        }
        throw new IllegalArgumentException("Y Coordinates must be more than -231");
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinateX, coordinateY);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {return false;}
        Coordinates that = (Coordinates) obj;
        return super.equals(obj) && Objects.equals(that.coordinateY, coordinateY) && Objects.equals(that.coordinateX, coordinateX);
    }

    @Override
    public String toString() {
        return String.format("Coordinates {coordinateX = %s, coordinateY = %s}", coordinateX, coordinateY);
    }
}
