package main.Commands;

import main.Utils.*;
import java.util.*;

public class Help implements Command {
    private final Map<String, CommandInfo> commands;

    public Help(Map<String, CommandInfo> commands) {this.commands = commands;}

    @Override
    public void execute() {

    }
}
