package com.ucast.tagmanager.entity;

/**
 * Created by pj on 2019/4/29.
 */
public class GetServerAffirm {
    String barcode;
    String tuoBanId;
    String status;
    String imgPath;
    String version;
    String voltage_1;
    String voltage_2;

    public GetServerAffirm() {
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTuoBanId() {
        return tuoBanId;
    }

    public void setTuoBanId(String tuoBanId) {
        this.tuoBanId = tuoBanId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVoltage_1() {
        return voltage_1;
    }

    public void setVoltage_1(String voltage_1) {
        this.voltage_1 = voltage_1;
    }

    public String getVoltage_2() {
        return voltage_2;
    }

    public void setVoltage_2(String voltage_2) {
        this.voltage_2 = voltage_2;
    }
}
