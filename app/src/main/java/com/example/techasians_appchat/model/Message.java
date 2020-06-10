package com.example.techasians_appchat.model;

public class Message {
    private String sender;
    private String receiver;
    private String message;
    private long time;
    private String type;

    public Message() {

    }

    public Message(String sender, String receiver, String message, long time, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
