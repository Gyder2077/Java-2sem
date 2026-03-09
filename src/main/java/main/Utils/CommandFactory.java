package main.Utils;

@FunctionalInterface
public interface CommandFactory {
    Command create(String[] args);
}
