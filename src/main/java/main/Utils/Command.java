package main.Utils;

import java.io.BufferedReader;

public interface Command {
    void execute();
    default boolean executeByScript(BufferedReader reader, FileManager fileManager) {return true;}
}
