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

    /**
     * Дополнительный элемент интерфейса, направленный на считывание составных типов данных из файла
     *
     * @param reader Буфер, хранящий последующие строки скрипта
     * @param fileManager Файловый менеджер
     */
    default boolean executeByScript(BufferedReader reader, FileManager fileManager) {return true;}
}
