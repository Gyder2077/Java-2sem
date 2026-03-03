package main;

import main.Given.*;
import main.Given.Enums.*;

import java.time.ZonedDateTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Coordinates coo1 = new Coordinates(1F, 2D);
        Event event1 = new Event(1, "namename", ZonedDateTime.now().plusDays(2), EventType.BASEBALL);
        Ticket ticket1 = new Ticket(1, "name", coo1, 123, true, TicketType.USUAL, event1);
        Coordinates coo2 = new Coordinates(2F, 1D);
        Event event2 = new Event(2, "emaneman", ZonedDateTime.now(), EventType.CONCERT);
        Ticket ticket2 = new Ticket(2, "eman", coo2, 321, false, TicketType.BUDGETARY, event2);

        Deque<Ticket> deque = new ArrayDeque<>(Arrays.asList(ticket2, ticket1));
        System.out.println("Исходный ArrayDeque: " + deque);

        // Шаг 1: Преобразуем в массив
        Ticket[] array = deque.toArray(new Ticket[0]);

        // Шаг 2: Сортируем массив (по возрастанию)
        Arrays.sort(array);

        // Шаг 3: Очищаем исходный ArrayDeque
        deque.clear();

        // Шаг 4: Добавляем отсортированные элементы обратно
        Collections.addAll(deque, array);

        System.out.println("Отсортированный ArrayDeque: " + deque);
    }
}
