package com.ucast.tagmanager.tools;

import com.alibaba.fastjson.JSON;
import com.ucast.tagmanager.R;
import com.ucast.tagmanager.entity.LoginMSg;
import com.ucast.tagmanager.eventBusMsg.AllThingOk;
import com.ucast.tagmanager.eventBusMsg.GetServiceCanActiveMsg;
import com.ucast.tagmanager.exception.ExceptionApplication;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by pj on 2019/1/29.
 */
public class HttpRequest {
//    public static String HOST = "";
    public static String HOST = "http://192.168.0.113:12907";
    public static String LOGIN_URL = HOST + "/Internal/Login";
    public static String SEND_READY_TO_WRITE_ID_URL = HOST + "/";
    public static String GET_DEVICE_STATUS = HOST + "/Tag/GetStatus";
    public static String SEND_WILL_CHANGE_DEVICE_MODE_URL = HOST + "/Tag/WaitActivate ";
    public static String SEND_CHANGE_DEVICE_MODE_URL = HOST + "/Tag/Activate";
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

    public static void sendWillChangeDeviceModeSatus(String barCode,String tuobancheId,String status,String path){
        RequestParams requestParams = new RequestParams(HttpRequest.SEND_WILL_CHANGE_DEVICE_MODE_URL);
        requestParams.addBodyParameter("ID",barCode);
        requestParams.addBodyParameter("Remarks",tuobancheId);
        requestParams.addBodyParameter("Status",status);
        requestParams.addBodyParameter("Picture",new File(path));
        requestParams.addHeader("Authorization","Basic " + savePasswd.get(MyTools.TOKEN));
        requestParams.setConnectTimeout(30 * 1000);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LoginMSg login = JSON.parseObject(result, LoginMSg.class);
                if (login.getMsgType().equals("1")){
                    EventBus.getDefault().postSticky(new GetServiceCanActiveMsg(true,""));
                }else {
                    if (login.getMsgType().equals("3")){
                        EventBus.getDefault().postSticky(new GetServiceCanActiveMsg(false, ExceptionApplication.getInstance().getString(R.string.token_error)));
                        return;
                    }
                    EventBus.getDefault().postSticky(new GetServiceCanActiveMsg(false,login.getInfo()));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                EventBus.getDefault().postSticky(new GetServiceCanActiveMsg(false,"请求超时，请重新操作" + ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });

    }

    public static void sendDeviceModeStatus(String barCode,String status){
        RequestParams requestParams = new RequestParams(HttpRequest.SEND_CHANGE_DEVICE_MODE_URL);
        requestParams.addBodyParameter("ID",barCode);
        requestParams.addBodyParameter("Status",status);
        requestParams.addHeader("Authorization","Basic " + savePasswd.get(MyTools.TOKEN));
        requestParams.setConnectTimeout(30 * 1000);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LoginMSg login = JSON.parseObject(result, LoginMSg.class);
                if (login.getMsgType().equals("1")){
                    EventBus.getDefault().postSticky(new AllThingOk(true,""));
                }else {
                    if (login.getMsgType().equals("3")){
                        EventBus.getDefault().postSticky(new AllThingOk(false, ExceptionApplication.getInstance().getString(R.string.token_error)));
                        return;
                    }
                    EventBus.getDefault().postSticky(new AllThingOk(false,login.getInfo()));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                EventBus.getDefault().postSticky(new AllThingOk(false,"请求超时  " + ex.getMessage()));
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
