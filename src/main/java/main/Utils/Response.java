package main.Utils;

import java.io.*;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String message;
    private final boolean success;

    public Response(String message) { this(message, true); }
    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String message() { return message; }
    public boolean success() { return success; }
}
