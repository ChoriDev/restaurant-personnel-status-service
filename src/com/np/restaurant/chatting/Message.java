package com.np.restaurant.chatting;

import java.io.Serializable;

import com.np.restaurant.user.User;

public class Message implements Serializable {
    private User sender;
    private User receiver;
    private String content;

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

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
