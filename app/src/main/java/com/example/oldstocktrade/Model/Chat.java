package com.example.oldstocktrade.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String type;
    private String id;
    private long time;

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }
}
