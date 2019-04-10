package com.ucast.tagmanager.entity;

/**
 * Created by Allen on 2017/6/28.
 */

public class LoginMSg {
    private String result;
    private String msg;
    private String info="";
    private PersonalMsg serviceman;
    private String overtime_id;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public PersonalMsg getServiceman() {
        return serviceman;
    }

    public void setServiceman(PersonalMsg serviceman) {
        this.serviceman = serviceman;
    }

    public String getOvertime_id() {
        return overtime_id;
    }

    public void setOvertime_id(String overtime_id) {
        this.overtime_id = overtime_id;
    }
}
