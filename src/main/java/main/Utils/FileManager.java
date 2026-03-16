package main.Utils;

import main.Given.Ticket;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileManager {
    public boolean writeXML(MyCollection collection, File filename) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filename), StandardCharsets.UTF_8)) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

            writer.write("<collection>\n");
            for (Ticket ticket : collection.getMyCollection()) {
                writer.write("  <ticket>\n");
                writer.write("    <id>" + ticket.getId() + "</id>\n");
                writer.write("    <name>" + ticket.getName() + "</name>\n");
                writer.write("    <coordinates>");
                writer.write("      <coordinateX>" + ticket.getCoordinates().getX() + "</coordinateX>\n");
                writer.write("      <coordinateY>" + ticket.getCoordinates().getY() + "</coordinateY>\n");
                writer.write("    </coordinates>\n");
                writer.write("    <creationDate>" + ticket.getCreationDate() + "</creationDate>\n");
                writer.write("    <price>" + ticket.getPrice() + "</price>\n");
                writer.write("    <refundable>" + ticket.getRefundable() + "</refundable>\n");
                writer.write("    <type>" + ticket.getType() + "</type>\n");
                writer.write("    <event>\n");
                writer.write("      <id>" + ticket.getEvent().getId() + "</id>\n");
                writer.write("      <name>" + ticket.getEvent().getName() + "</name>\n");
                writer.write("      <date>" + ticket.getEvent().getDate() + "</date>\n");
                writer.write("      <eventType>" + ticket.getEvent().getEventType() + "</eventType>\n");
                writer.write("    </event>\n");
                writer.write("  </ticket>\n");
            }

            writer.write("  <history>\n");
            for (String command : collection.getHistory()) {
                writer.write("      <command>" + command + "</command>\n");
            }
            writer.write("  </history>\n");

            writer.write("  <creationDate>" + collection.getDateTime() + "</creationDate>\n");
            writer.write("  <nextId>" + collection.getNextId() + "</nextId>\n");

            writer.write("</collection>\n");

            System.out.println("XML file was written successfully");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
