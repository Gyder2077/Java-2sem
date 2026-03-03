package main.Given;

import java.util.Objects;

public class Coordinates {
    private Float x; //Поле не может быть null
    private Double y; //Значение поля должно быть больше -231, Поле не может быть null

    public Coordinates(Float x, Double y) {
        valid(x, y);
    }

    public void valid(Float x, Double y) {
        if (!(x == null) && !(y == null || y <= -231.0)) {
            this.x = x;
            this.y = y;
            return;
        }
        throw new IllegalArgumentException("Illegal Coordinates");
    }

    public Float getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public void setX(Float x) {
        if (!(x == null)) {
            this.x = x;
            return;
        }
        throw new IllegalArgumentException("Illegal X Coordinates");
    }

    public void setY(Double y) {
        if (!(y == null || y <= -231.0)) {
        this.y = y;
        return;
    }
        throw new IllegalArgumentException("Illegal Y Coordinates");
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {return false;}
        Coordinates that = (Coordinates) obj;
        return super.equals(obj) && Objects.equals(that.y, y) && Objects.equals(that.x, x);
    }

    @Override
    public String toString() {
        return String.format("Coordinates{x='%s', y='%s'}", x, y);
    }
}
