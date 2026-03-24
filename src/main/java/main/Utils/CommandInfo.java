package main.Utils;

/**
 * Запись, хранящая всю информацию о конкретной команде
 *
 * @see Command
 * @see CommandFactory
 *
 * @param name Имя команды
 * @param description Описание команды
 * @param factory Фабрика (Функциональный интерфейс)
 */
public record CommandInfo(String name, String description, CommandFactory factory) { }
