package com.ucast.tagmanager.entity;

import com.ucast.tagmanager.exception.CrashHandler;

/**
 * Created by pj on 2019/1/28.
 */
public class Config {
    public static int PORTBAUDRATE = 115200;
    public static String PORTPATHE = "";
    public static boolean ISDEBUG = true;
    public static String LOGPATH = CrashHandler.ALBUM_PATH + "/simpleLog.txt";
    public static String LOGPATHWITHTIME = CrashHandler.ALBUM_PATH + "/simpleLogWithTime.txt";
}
