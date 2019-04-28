package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/4/28.
 */
public class CanReadDianYaEvent {
    boolean isCanReadDianYa;

    public CanReadDianYaEvent(boolean isCanReadDianYa) {
        this.isCanReadDianYa = isCanReadDianYa;
    }

    public boolean isCanReadDianYa() {
        return isCanReadDianYa;
    }

    public void setCanReadDianYa(boolean canReadDianYa) {
        isCanReadDianYa = canReadDianYa;
    }
}
