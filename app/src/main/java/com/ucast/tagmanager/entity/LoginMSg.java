package com.ucast.tagmanager.entity;

/**
 * Created by Allen on 2017/6/28.
 */

public class LoginMSg {
    private String MsgType ;
    private String Info;

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }
}
