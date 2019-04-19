package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/4/18.
 */
public class ErrorOperateMsg {
    String showMsg;

    public ErrorOperateMsg(String showMsg) {
        this.showMsg = showMsg;
    }

    public String getShowMsg() {
        return showMsg;
    }

    public void setShowMsg(String showMsg) {
        this.showMsg = showMsg;
    }
}
