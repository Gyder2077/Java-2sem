package main.Commands;

public interface Command {
    void execute(String[] args);
    String toString();
}
