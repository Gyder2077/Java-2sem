package main.Utils;

import main.Server.MyCollection;

import java.io.Serializable;

/**
 * Абстракция - основа Command Pattern
 */
public interface Command extends Serializable {
    /**
     * Основной метод абстракции
     */
    Response execute(MyCollection myCollection);
}
