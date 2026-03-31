package main.Utils;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import main.Given.Enums.*;
import main.Given.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * Класс представляет менеджер для обработки файловых данных
 */
public class FileManager {
    private String filename;

    public FileManager() {
        filename = System.getenv("COLLECTION");
        if (filename == null || filename.trim().isEmpty()) {
            filename = "Collection.xml";
            System.out.println("Environment variable 'COLLECTION' was not set. Using default file: " + filename);
        } else System.out.println("Using file from environment: " + filename);
    }

    /**
     * Сохраняет все данные о коллекции в xml файл
     *
     * @param collection Коллекция
     */
    public boolean writeXML(MyCollection collection) {
        try {
            XmlMapper xmlMapper = XmlMapper.builder()
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .build();
            xmlMapper.registerModule(new JavaTimeModule());
            xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            xmlMapper.writeValue(new File(filename), collection);
            System.out.println("XML file was written successfully");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Считывает все данные о коллекции из xml файла
     */
    public MyCollection parseXML() {
        try {
            XmlMapper xmlMapper = XmlMapper.builder()
                    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                    .build();
            xmlMapper.registerModule(new JavaTimeModule());
            xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            MyCollection myCollection = xmlMapper.readValue(new File(filename), MyCollection.class);
            System.out.println("XML file was read successfully");
            return myCollection;
        } catch (IOException e) {
            e.printStackTrace();
            return new MyCollection();
        }
    }

    /**
     * Считывает все поля объекта T из текстового файла по его set методам
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
