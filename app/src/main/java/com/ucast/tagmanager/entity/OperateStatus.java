package com.ucast.tagmanager.entity;

/**
 * Created by pj on 2019/4/18.
 */
public enum OperateStatus {
    SLEEP(1,"睡眠模式"),
    IFACTIVED(2,"请求能否转换为工作模式"),
    GETCANACTIVED(3,"能够转换为工作模式"),
    GOTOACTIVED(4,"正在转换为工作模式"),
    ACTIVED(5,"工作模式"),
    GOTOSLEEP(0,"正在转换为睡眠模式");

    public static String WAITACTIVATESTR = "WaitActivate";
    public static String ACTIVATESTR = "Activate";

    private int level;
    private String name;
    private OperateStatus(int level,String name){
        this.level = level;
        this.name = name;
    }

    public int getLevel(){
        return this.level;
    }

    public String getName(){
        return this.name;
    }
}
