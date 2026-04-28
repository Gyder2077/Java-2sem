package main.Utils;

import java.io.*;

public record Response(String message) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
