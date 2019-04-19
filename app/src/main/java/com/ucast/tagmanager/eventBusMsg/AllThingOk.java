package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/4/18.
 */
public class AllThingOk {
    String info;
    boolean status;

    public AllThingOk(boolean status,String info ) {
        this.info = info;
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
