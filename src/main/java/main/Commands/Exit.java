package main.Commands;

import main.Utils.Command;

/**
 * Класс команда для остановки работы программы без сохранения
 */
public class Exit implements Command {
    public Exit() {}

    @Override
    public void execute() {
        System.out.println("Exiting the program...");
        System.exit(0);
    }
}
