package com.np.restaurant;

import java.io.Serializable;

public class Command implements Serializable {
    private String command;

    public Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
