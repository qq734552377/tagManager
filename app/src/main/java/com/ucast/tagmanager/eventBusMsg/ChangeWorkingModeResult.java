package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/1/29.
 */
public class ChangeWorkingModeResult {
    boolean isOK = false;

    public ChangeWorkingModeResult(boolean isOK) {
        this.isOK = isOK;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }
}
