package main.Utils;


import java.io.Serializable;

/**
 * Функциональный интерфейс для обработки аргументов команд
 *
 * @see Command
 */

public interface CommandFactory extends Serializable {
    /**
     * Создание команды исходя из переданных аргументов
     *
     * @param args Массив аргументов
     */
    Command create(String[] args);
}
