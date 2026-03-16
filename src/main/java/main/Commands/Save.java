package main.Commands;

import main.Utils.*;

import java.io.File;

public class Save implements Command {
    private final MyCollection myCollection;

    public Save(MyCollection myCollection) {this.myCollection = myCollection;}

    @Override
    public void execute() {
        FileManager fileManager = new FileManager();
        if (!fileManager.writeXML(myCollection, new File("Collection.xml"))) System.out.println("Unexpected EROR, please try again");
    }
}
