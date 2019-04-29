package com.ucast.tagmanager.tools;

import com.alibaba.fastjson.JSON;
import com.ucast.tagmanager.R;
import com.ucast.tagmanager.entity.GetServerAffirm;
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
    public static String HOST = "http://180.240.240.117:12347";
//    public static String HOST = "http://192.168.0.31:12347";
//    public static String HOST = "http://58.246.122.118:12347";
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

    public static void sendWillChangeDeviceModeSatus(GetServerAffirm affirm){
        RequestParams requestParams = new RequestParams(HttpRequest.SEND_WILL_CHANGE_DEVICE_MODE_URL);
        requestParams.addBodyParameter("ID",affirm.getBarcode());
        requestParams.addBodyParameter("Remarks",affirm.getTuoBanId());
        requestParams.addBodyParameter("Status",affirm.getStatus());
        requestParams.addBodyParameter("Version",affirm.getVersion());
        requestParams.addBodyParameter("Voltage_1",affirm.getVoltage_1());
        requestParams.addBodyParameter("Voltage_2",affirm.getVoltage_2());
        requestParams.addBodyParameter("Picture",new File(affirm.getImgPath()));
        requestParams.addHeader("Authorization","Basic " + savePasswd.get(MyTools.TOKEN));
        requestParams.setConnectTimeout(30 * 1000);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LoginMSg login = JSON.parseObject(result, LoginMSg.class);
                if (login.getMsgType().equals("1")){
                    EventBus.getDefault().postSticky(new GetServiceCanActiveMsg(true,ExceptionApplication.getInstance().getString(R.string.active_get_server_command)));
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
                EventBus.getDefault().postSticky(new GetServiceCanActiveMsg(false,"请求超时，请重新操作"));
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });

    }

    public static void sendDeviceModeStatus(final String barCode, String status){
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
                    EventBus.getDefault().postSticky(new AllThingOk(true,ExceptionApplication.getInstance().getString(R.string.active_all_ok),barCode));
                }else {
                    if (login.getMsgType().equals("3")){
                        EventBus.getDefault().postSticky(new AllThingOk(false, ExceptionApplication.getInstance().getString(R.string.token_error),barCode));
                        return;
                    }
                    EventBus.getDefault().postSticky(new AllThingOk(false,login.getInfo(),barCode));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                EventBus.getDefault().postSticky(new AllThingOk(false,ExceptionApplication.getInstance().getString(R.string.timeout),barCode));
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
