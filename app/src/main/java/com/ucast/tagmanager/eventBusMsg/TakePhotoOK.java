package com.ucast.tagmanager.eventBusMsg;

/**
 * Created by pj on 2019/4/18.
 */
public class TakePhotoOK {
    String path;

    public TakePhotoOK(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
