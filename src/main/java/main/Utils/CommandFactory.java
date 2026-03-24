package main.Utils;


/**
 * Функциональный интерфейс для обработки аргументов команд
 *
 * @see Command
 */

@FunctionalInterface
public interface CommandFactory {
    /**
     * Создание команды исходя из переданных аргументов
     *
     * @param args Массив аргументов
     */
    Command create(String[] args);
}
