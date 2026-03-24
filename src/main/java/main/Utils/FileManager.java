package main.Utils;

import main.Given.Enums.*;
import main.Given.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * Класс представляет менеджер для обработки файловых данных
 */
public class FileManager {
    public FileManager() {}

    /**
     * Сохраняет все данные о коллекции в xml файл
     *
     * @param collection Коллекция
     */
    public boolean writeXML(MyCollection collection) {
        String filename = System.getenv("COLLECTION");
        if (filename == null || filename.trim().isEmpty()) {
            filename = "Collection.xml";
            System.out.println("Environment variable 'COLLECTION' was not set. Using default file: " + filename);
        } else System.out.println("Using file from environment: " + filename);

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filename), StandardCharsets.UTF_8)) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<collection>\n");

            writer.write("  <tickets>\n");
            for (Ticket ticket : collection.getMyCollection()) {
                writer.write("    <ticket>\n");
                writer.write("      <id>" + ticket.getId() + "</id>\n");
                writer.write("      <name>" + xmlFormat(ticket.getName()) + "</name>\n");
                writer.write("      <coordinates>\n");
                writer.write("         <coordinateX>" + ticket.getCoordinates().getX() + "</coordinateX>\n");
                writer.write("         <coordinateY>" + ticket.getCoordinates().getY() + "</coordinateY>\n");
                writer.write("      </coordinates>\n");
                writer.write("      <creationDate>" + ticket.getCreationDate() + "</creationDate>\n");
                writer.write("      <price>" + ticket.getPrice() + "</price>\n");
                writer.write("      <refundable>" + ticket.getRefundable() + "</refundable>\n");
                writer.write("      <type>" + ticket.getType() + "</type>\n");
                writer.write("      <event>\n");
                writer.write("         <eventId>" + ticket.getEvent().getId() + "</eventId>\n");
                writer.write("         <eventName>" + xmlFormat(ticket.getEvent().getName()) + "</eventName>\n");
                writer.write("         <date>" + ticket.getEvent().getDate() + "</date>\n");
                writer.write("         <eventType>" + ticket.getEvent().getEventType() + "</eventType>\n");
                writer.write("      </event>\n");
                writer.write("  </ticket>\n");
            }
            writer.write("  </tickets>\n");

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

    /**
     * Считывает все данные о коллекции из xml файла
     *
     * @param myCollection Коллекция
     */
    public boolean parseXML(MyCollection myCollection) {
        String filename = System.getenv("COLLECTION");
        if (filename == null || filename.trim().isEmpty()) {
            filename = "Collection.xml";
            System.out.println("Environment variable 'COLLECTION' was not set. Using default file: " + filename);
        } else System.out.println("Using file from environment: " + filename);

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8)) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(reader);
            inputSource.setEncoding(StandardCharsets.UTF_8.name());

            Document doc = builder.parse(inputSource);

            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            Element ticketsElement = getFirstChildElement(root, "tickets");
            if (ticketsElement != null) {
                ArrayDeque<Ticket> collection = new ArrayDeque<>();
                NodeList nodeList = ticketsElement.getElementsByTagName("ticket");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node ticket = nodeList.item(i);
                    if (ticket.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) ticket;

                        long id = Long.parseLong(getTextContent(element, "id"));
                        String name = getTextContent(element, "name");

                        Element coords = getFirstChildElement(element, "coordinates");
                        Coordinates coordinates = null;
                        if (coords != null) {
                            Float x = Float.parseFloat(getTextContent(coords, "coordinateX"));
                            Double y = Double.parseDouble(getTextContent(coords, "coordinateY"));
                            coordinates = new Coordinates(x, y);
                        }
                        ZonedDateTime creationDate = ZonedDateTime.parse(
                                getTextContent(element, "creationDate"));
                        int price = Integer.parseInt(getTextContent(element, "price"));
                        boolean refundable = Boolean.parseBoolean(getTextContent(element, "boolean"));
                        String ticketType = getTextContent(element, "type");
                        TicketType type = null;
                        if (!Objects.equals(ticketType, "null")) type = Enum.valueOf(TicketType.class, ticketType);

                        Element ev = getFirstChildElement(element, "event");
                        Event event = null;
                        if (ev != null) {
                            long eventId = Long.parseLong(getTextContent(element, "eventId"));
                            String eventName =  getTextContent(element, "eventName");
                            ZonedDateTime date = ZonedDateTime.parse(getTextContent(element, "date"));
                            String eventType = getTextContent(element, "eventType");
                            EventType eventType1 = null;
                            if (!Objects.equals(eventType, "null")) {
                                eventType1 = Enum.valueOf(EventType.class, eventType);
                            }
                            event = new Event(eventId, eventName, date, eventType1);
                        }

                        collection.add(new Ticket(id, name, coordinates, creationDate, price, refundable, type, event));
                    }
                }
                myCollection.setMyCollection(collection);
            }

            Element history = getFirstChildElement(root, "history");
            if (history != null) {
                ArrayDeque<String> hst = new ArrayDeque<>();
                NodeList nodeList = history.getElementsByTagName("command");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node command = nodeList.item(i);
                    if (command.getNodeType() == Node.ELEMENT_NODE) {
                        String cmd = command.getTextContent().trim();
                        hst.add(cmd);
                    }
                }
                myCollection.setHistory(hst);
            }

            ZonedDateTime dateTime = ZonedDateTime.parse(getTextContent(root, "creationDate"));
            myCollection.setDateTime(dateTime);

            long nextId = Long.parseLong(getTextContent(root, "nextId"));
            myCollection.setNextId(nextId);

            System.out.println("The collection was parsed successfully");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Считывает все поля объекта T из текстового файла по его set методам
     *
     * @param instance
     *
     * @see FileManager {@link #readField(Object, String, Class, BufferedReader)}
     */
    public <T> T readObject(T instance, BufferedReader reader) {
        Stream<Method> setterArray = Stream.of(instance.getClass().getMethods())
                .filter(
                        e ->
                                e.getName().startsWith("set") &&
                                        e.getParameterCount() == 1 &&
                                        !Modifier.isStatic(e.getModifiers())
                )
                .sorted(Comparator.comparing(Method::getName));
        for (Object setter : setterArray.toArray()) {
            Method tmpSetter = (Method) setter;
            String tmpSetterName = tmpSetter.getName();
            Class<?> tmpFieldType = tmpSetter.getParameterTypes()[0];
            while (true) {
                try {
                    if (readField(instance, tmpSetterName, tmpFieldType, reader)) break;
                } catch (RuntimeException e) {
                    return null;
                }
            }
        }
        return instance;
    }

    /**
     * Непосредственная обработка поля некого объекта по его setter
     *
     * @see FileManager {@link #readObject(Object, BufferedReader)}
     */
    private boolean readField(Object instance, String setterName, Class<?> fieldType, BufferedReader reader) {
        Object result = null;
        String parsedInput = "";
        Method setterMethod;
        try {
            setterMethod = instance.getClass().getMethod(setterName, fieldType);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unexpected EROR");
        }
        try {
            if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                parsedInput = reader.readLine();
                result = Integer.valueOf(parsedInput.trim());
            } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                parsedInput = reader.readLine();
                result = Long.valueOf(parsedInput.trim());
            } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                parsedInput = reader.readLine();
                result = Double.valueOf(parsedInput.trim());
            } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                parsedInput = reader.readLine();
                result = Float.valueOf(parsedInput.trim());
            } else if (fieldType.equals(String.class)) {
                parsedInput = reader.readLine();
                result = parsedInput.trim();
            } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                parsedInput = reader.readLine();
                if (parsedInput.equals("false") || parsedInput.equals("true")) result = Boolean.valueOf(parsedInput);
                else {
                    return false;
                }
            } else if (fieldType.equals(ZonedDateTime.class)) {
                parsedInput = reader.readLine();
                result = ZonedDateTime.parse(parsedInput.trim());
            } else if (fieldType.isEnum()) {
                parsedInput = reader.readLine().trim();
                if (!parsedInput.isEmpty()) result = Enum.valueOf((Class<Enum>) fieldType, parsedInput);
            }
        } catch (Exception e) {
            return false;
        }
        if (result != null || fieldType.isEnum()) {
            if (fieldType.equals(String.class) && ((String) result).isEmpty()) {
                result = null;
            }
            try {
                setterMethod.invoke(instance, result);
                return true;
            } catch (InvocationTargetException | IllegalAccessException e) {
                return false;
            }
        }

        Object tmpObject;
        try {
            tmpObject = fieldType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            try {
                tmpObject = fieldType.getDeclaredConstructor(long.class).newInstance(((Ticket) instance).getId());
            } catch (Exception ex) {
                throw new RuntimeException("Unexpected EROR");
            }
        }
        readObject(tmpObject, reader);
        try {
            setterMethod.invoke(instance, tmpObject);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    /**
     * Утилита для упрощения парсинга xml файла
     */
    private String getTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

    /**
     * Утилита для упрощения парсинга xml файла
     */
    private Element getFirstChildElement(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0 && list.item(0) instanceof Element) {
            return (Element) list.item(0);
        }
        return null;
    }

    /**
     * Утилита для корректного записи xml файла
     */
    private String xmlFormat(Object obj) {
        if (obj == null) return "";
        String text = (String) obj;
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
