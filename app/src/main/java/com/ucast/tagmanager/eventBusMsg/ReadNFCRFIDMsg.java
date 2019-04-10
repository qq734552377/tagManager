package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/1/29.
 */
public class ReadNFCRFIDMsg {
    String msg;

    public ReadNFCRFIDMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
