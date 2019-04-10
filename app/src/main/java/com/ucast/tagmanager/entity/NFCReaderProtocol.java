package com.ucast.tagmanager.entity;

import com.ucast.tagmanager.tools.MyTools;

/**
 * Created by pj on 2019/2/25.
 */
public class NFCReaderProtocol {

    /**
     * 关闭所有的自动寻卡模式
     * */
    public static byte[] getShutdownAutoCheckNfCId(){
        byte[] res = new byte[6];
        res[0] = 0x68;
        res[1] = 0x03;
        res[2] = 0x0D;
        res[3] = 0x00;
        res[4] = 0x00;
        res[5] = 0x00;
        return MyTools.getSendData(res);
    }
    /**
     * 设置A卡读写模式
     * */
    public static byte[] getSetACardMode(){
        byte[] res = new byte[3];
        res[0] = 0x68;
        res[1] = 0x00;
        res[2] = 0x1E;
        return MyTools.getSendData(res);
    }

    /**
     * 读取A卡的指定block的数据
     * */
    public static byte[] getReadMF0DataByBlock(int blockId){
        byte[] res = new byte[4];
        res[0] = 0x68;
        res[1] = 0x01;
        res[2] = 0x28;
        res[3] = (byte)(blockId & 0xFF);
        return MyTools.getSendData(res);
    }

    /**
     * 写入A卡指定区域指定数据
     * */
    public static byte[] getWriteMF0DataByBlockId(int blockId ,byte... data){
        byte[] res = new byte[8];
        res[0] = 0x68;
        res[1] = 0x01;
        res[2] = 0x29;
        res[3] = (byte)(blockId & 0xFF);
        System.arraycopy(data,0,res,4,data.length < 4 ? data.length : 4);
        return MyTools.getSendData(res);
    }

}
