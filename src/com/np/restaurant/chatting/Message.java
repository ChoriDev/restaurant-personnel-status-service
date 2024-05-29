package com.np.restaurant.chatting;

import java.io.Serializable;
import com.np.restaurant.user.User;

public class Message implements Serializable {
    private final User sender;
    private final User receiver;
    private final String content;

    public Message(User sender, User receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { content = content;}



    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender.getName() +
                ", receiver=" + (receiver != null ? receiver.getName() : "null") +
                ", content='" + content + '\'' +
                '}';
    }
}