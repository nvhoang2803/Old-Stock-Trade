package com.example.oldstocktrade.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String type;
    private String id;
    private long time;
    private Boolean seen;

    public Boolean getSeen() {
        return seen;
    }

    public Chat(String sender, String receiver, String message, String type, String id, long time, Boolean seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.id = id;
        this.time = time;
        this.seen = seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Chat() {
    }

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
