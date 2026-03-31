package main.Utils;

import java.io.BufferedReader;

/**
 * Абстракция - основа Command Pattern
 */
public interface Command {

    /**
     * Основной метод абстракции
     */
    void execute();
}
