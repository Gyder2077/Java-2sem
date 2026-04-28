package main.Server;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;

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
}
