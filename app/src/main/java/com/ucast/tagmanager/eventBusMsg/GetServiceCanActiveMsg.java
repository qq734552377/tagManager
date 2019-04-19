package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/4/18.
 */
public class GetServiceCanActiveMsg {
    boolean canActived;
    String info;

    public GetServiceCanActiveMsg(boolean canActived) {
        this.canActived = canActived;
    }

    public GetServiceCanActiveMsg(boolean canActived, String info) {
        this.canActived = canActived;
        this.info = info;
    }

    public boolean isCanActived() {
        return canActived;
    }

    public void setCanActived(boolean canActived) {
        this.canActived = canActived;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
