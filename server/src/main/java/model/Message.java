package model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    String message;
    String sender;
    String recipient;
    Date date;

    public Message(String message, String sender, String recipient, Date date) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
        this.date = date;
    }

    public Message(String message, String recipient) {
        this.message = message;
        this.recipient = recipient;
    }

    public Message() {}

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

    public void setDate(Date date) { this.date = date; }

    public Date getDate() {  return date; }
}
