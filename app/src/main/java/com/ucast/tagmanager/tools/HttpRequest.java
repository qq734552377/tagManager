package com.ucast.tagmanager.tools;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.ucast.tagmanager.MainActivity;
import com.ucast.tagmanager.R;
import com.ucast.tagmanager.activities.LoginActivity;
import com.ucast.tagmanager.entity.LoginMSg;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by pj on 2019/1/29.
 */
public class HttpRequest {
    public static String HOST = "";
    public static String LOGIN_URL = HOST + "/";
    public static String SEND_READY_TO_WRITE_ID_URL = HOST + "/";
    public static String GET_DEVICE_STATUS = HOST + "/";
    public static String SEND_DEVICE_RESULT_URL = HOST + "/";
    public static SavePasswd savePasswd = SavePasswd.getInstace();


    public static void getDeviceStatus(String barCode){
        RequestParams requestParams = new RequestParams(HttpRequest.GET_DEVICE_STATUS);
        requestParams.addBodyParameter("barCode",barCode);
        requestParams.addHeader("Authorization","Basic " + savePasswd.get(MyTools.TOKEN));
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });

    }

    public static void sendDeviceModeSatus(String barCode){
        RequestParams requestParams = new RequestParams(HttpRequest.SEND_DEVICE_RESULT_URL);
        requestParams.addBodyParameter("barCode",barCode);
        requestParams.addHeader("Authorization","Basic " + savePasswd.get(MyTools.TOKEN));
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });

    }

}
