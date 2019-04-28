package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/1/29.
 */
public class ChangeWorkingModeResult {
    boolean isOK = false;
    String rfid;
    public ChangeWorkingModeResult(boolean isOK,String rfid) {
        this.isOK = isOK;
        this.rfid = rfid;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }
}
