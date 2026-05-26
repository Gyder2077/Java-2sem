package main.Server;

import main.Model.Enums.TicketType;
import main.Model.Ticket;
import main.Utils.Response;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Хранилище коллекции билетов в памяти.
 * Адаптировано под БД: убран XML, добавлена синхронизация, убран ручной nextId.
 */
public class MyCollection {

    private final Collection<Ticket> tickets = Collections.synchronizedCollection(new LinkedHashSet<>());

    private final ArrayDeque<String> history = new ArrayDeque<>();
    private final ZonedDateTime creationDate = ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    public MyCollection() {}

    public Collection<Ticket> getTickets() { return tickets; }
    public ArrayDeque<String> getHistory() { return history; }
    public ZonedDateTime getCreationDate() { return creationDate; }

    public void addElement(Ticket ticket) {
        synchronized (tickets) {
            tickets.add(ticket);
            List<Ticket> sorted = tickets.stream().sorted().toList();
            tickets.clear();
            tickets.addAll(sorted);
        }
    }

    /**
     * Удаляет элемент по ID. Возвращает true, если удаление прошло успешно.
     * Не выбрасывает исключения, чтобы не ломать цепочку syncToDatabase.
     */
    public boolean delElement(long id) {
        synchronized (tickets) {
            return tickets.removeIf(t -> t.getId() == id);
        }
    }

    public void clearCollection() {
        synchronized (tickets) {
            tickets.clear();
        }
    }

    public Response showAll(boolean descending) {
        synchronized (tickets) {
            String header = "All elements from the collection" + (descending ? " in reverse:\n" : ":\n");
            Stream<Ticket> stream = tickets.stream();
            if (descending) stream = stream.sorted(Comparator.reverseOrder());
            String body = stream.map(Ticket::toString).collect(Collectors.joining("\n"));
            return new Response(header + body);
        }
    }

    public Response filtered(String name) {
        synchronized (tickets) {
            var list = tickets.stream()
                    .filter(t -> t.getName() != null && t.getName().contains(name))
                    .toList();
            if (list.isEmpty()) return new Response("Элементы с таким именем не найдены", false);
            return new Response("Отфильтрованные элементы:\n" + list.stream().map(Ticket::toString).collect(Collectors.joining("\n")));
        }
    }

    public Response filtered(TicketType type) {
        synchronized (tickets) {
            var list = tickets.stream()
                    .filter(t -> t.getType() != null && t.getType().ordinal() <= type.ordinal())
                    .toList();
            if (list.isEmpty()) return new Response("Элементы с таким типом не найдены", false);
            return new Response("Отфильтрованные элементы:\n" + list.stream().map(Ticket::toString).collect(Collectors.joining("\n")));
        }
    }

    public boolean containsTicket(long id) {
        synchronized (tickets) {
            return tickets.stream().anyMatch(t -> t.getId() == id);
        }
    }
}