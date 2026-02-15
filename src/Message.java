package src;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable { // Every object passed between JVMs must implement Serializable
 
    private String sender;              // Username of sender
    private String content;             // Actual message text
    private LocalDateTime timestamp;    // Time message was created

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();  // Capture current time
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");

        return "[" + timestamp.format(formatter) + "] "
                + sender + ": "
                + content;
    }
}
