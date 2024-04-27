package com.np.restaurant;

import java.io.Serializable;

public class SuccessFlag implements Serializable {
    private boolean flag;

    public SuccessFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }
}
