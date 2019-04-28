package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/4/18.
 */
public class AllThingOk {
    String info;
    boolean status;
    String barCode;

    public AllThingOk(boolean status,String info,String barCode ) {
        this.info = info;
        this.status = status;
        this.barCode = barCode;
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

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
