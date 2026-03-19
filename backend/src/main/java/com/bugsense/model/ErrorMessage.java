package com.bugsense.model;

public class ErrorMessage {
    private int line;
    private String type;
    private String message;

    public ErrorMessage(int line, String type, String message) {
        this.line = line;
        this.type = type;
        this.message = message;
    }

    public int getLine() { return line; }
    public String getType() { return type; }
    public String getMessage() { return message; }
}