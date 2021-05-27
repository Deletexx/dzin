package main;

import java.util.Date;

public class Message {
    String message;
    String sender;
    String recipient;
    Date date;

    public Message(String message, String sender, String recipient, Date date) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
        this.date = new Date();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
