package com.ucast.tagmanager.tools;

import com.ucast.tagmanager.exception.CrashHandler;

/**
 * Created by pj on 2019/1/28.
 */
public class Config {
    public static int PORTBAUDRATE = 115200;
    public static String PORTPATHE = "";
    public static boolean USESTRINGPATH =true;

    public static String LOGPATH = CrashHandler.ALBUM_PATH + "/simpleLog.txt";
    public static String ACTIVEOKCSVPATH = CrashHandler.ALBUM_PATH + "/activeOk.csv";
    public static String CAMERA = CrashHandler.ALBUM_PATH + "/camera/";
}
