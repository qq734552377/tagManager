package com.ucast.tagmanager;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ucast.tagmanager.entity.NFCReaderProtocol;
import com.ucast.tagmanager.eventBusMsg.ChangeSleepModeResult;
import com.ucast.tagmanager.eventBusMsg.ChangeWorkingModeResult;
import com.ucast.tagmanager.eventBusMsg.NFCServiceStatusMsg;
import com.ucast.tagmanager.eventBusMsg.ReadModeRusult;
import com.ucast.tagmanager.eventBusMsg.ReadNFCRFIDMsg;
import com.ucast.tagmanager.eventBusMsg.WriteNFCResult;
import com.ucast.tagmanager.serial.OpenPrint;
import com.ucast.tagmanager.tools.MyDialog;
import com.ucast.tagmanager.tools.MyTools;
import com.ucast.tagmanager.view.mysaomiao.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public String scanResult = "";
    public ProgressDialog progressDialog = null;
    public int readRfidToHandleType = 0;
    public int readModeToHandleType = 0;

    public byte[] SEND_WILL_CHANGE_RFID = {
            (byte) 0xA2,//写
            (byte) 0x09,//页数
            (byte) 0x41,//A
            (byte) 0x35,//5
            (byte) 0x35,//5
            (byte) 0x61 //a
    };

    byte[] SEND_CHANGE_RFID = {
            (byte) 0xA2,//写
            (byte) 0x04,//页数
            (byte) 0x03,
            (byte) 0x2D,
            (byte) 0x01,
            (byte) 0x00
    };
    byte[] SEND_CHANGE_TO_SLEEP = {
            (byte) 0xA2,//写
            (byte) 0x05,//页数
            (byte) 0x6F,//o
            (byte) 0x66,//f
            (byte) 0x66,//f
            (byte) 0x00
    };
    byte[] SEND_CHANGE_TO_WORK = {
            (byte) 0xA2,//写
            (byte) 0x05,//页数
            (byte) 0x6F,//o
            (byte) 0x6E,//n
            (byte) 0x00,
            (byte) 0x00
    };
    byte[] SEND_FANG_CHAI = {
            (byte) 0xA2,//写
            (byte) 0x07,//页数
            (byte) 0x41,//A
            (byte) 0x62,//b
            (byte) 0x43,//C
            (byte) 0x64 //d
    };

    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;

    @ViewInject(R.id.scan_code)
    TextView barCodeTextview;
    @ViewInject(R.id.rfid)
    TextView rfidTextview;
    @ViewInject(R.id.match_result)
    TextView matchResultTextview;
    @ViewInject(R.id.msg)
    TextView msgResultTextview;

    //设备列表
    private HashMap<String, UsbDevice> deviceList;
    //USB管理器:负责管理USB设备的类
    private UsbManager manager;
    //找到的USB设备
    private UsbDevice mUsbDevice;
    //代表USB设备的一个接口
    private UsbInterface mInterface = null;
    private UsbDeviceConnection mDeviceConnection;
    //代表一个接口的某个节点的类:写数据节点
    private UsbEndpoint usbEpOut;
    //代表一个接口的某个节点的类:读数据节点
    private UsbEndpoint usbEpIn;
    //要发送信息字节
    private byte[] sendbytes;
    //接收到的信息字节
    private byte[] receiveytes;

    int mVendorID = 1659;
    int mProductID = 8963;

    private boolean isRead = true;

    public void initTextViewMsg(){
        barCodeTextview.setText("");
        rfidTextview.setText("");
        matchResultTextview.setText("");
        msgResultTextview.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        EventBus.getDefault().register(this);
        initNFC();
    }

    @Event(R.id.scan)
    private void startScan(View v){
        startAc(0);
    }
    @Event(R.id.write_rfid)
    private void writeNFC(View v){
        isRead = false;
        showToast("现在能写数据了");
    }
    @Event(R.id.read_rfid)
    private void readNFC(View v){
        isRead = true;
        showToast("现在能读取四区数据了");
    }
    @Event(R.id.change_to_sleep)
    private void change_to_sleep(View v){
        initTextViewMsg();
        readModeToHandleType = R.id.change_mode_to_sleep;
        checkRfidToChangeMode();
    }
    @Event(R.id.change_to_work)
    private void change_to_work(View v){
        initTextViewMsg();
        readModeToHandleType = R.id.change_mode_to_work;
        checkRfidToChangeMode();
    }
    @Event(R.id.check_rfid)
    private void check_rfid(View v){
        checkRfid();
    }

    public void startAc(int type) {
        scanResult = "";
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        intent.putExtra(CaptureActivity.CAMERAKEY, type);
        startActivityForResult(intent, type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        boolean isOK;
        switch (resultCode) {
            case 0://侧面摄像头
                scanResult = data.getStringExtra(CaptureActivity.RESULT);
                //todo 判断扫描结果长度  长度正确读取RFID

                barCodeTextview.setText(getString(R.string.barcode) + ":" + scanResult);
                setProgressDialogMsgAndShow("请将手机贴近NFC卡片，听到声音后等待一秒");
                break;
            case 1://前面摄像头
                scanResult = data.getStringExtra(CaptureActivity.RESULT);
                barCodeTextview.setText(getString(R.string.barcode) + ":" + scanResult);
                break;
        }

    }

    public void checkRfid(){
        readRfidToHandleType = R.id.read_rfid_to_check;
        startAc(0);
    }
    public void checkRfidToChangeMode(){
        readRfidToHandleType = R.id.read_rfid_to_change_mode;
        startAc(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//读取到RFID后的操作
    public void readRfidResultHandle(ReadNFCRFIDMsg readNFCMsg){
        // TODO 读取rfid 检测RFID与扫描得到的条形码比对 不相同直接返回
        msgResultTextview.setText(readNFCMsg.getMsg());
        dismssProgress();
        switch (readRfidToHandleType){
            case R.id.read_rfid_to_check:
                //TODO 回馈结果给操作者

                break;

            case R.id.read_rfid_to_change_mode:
                //todo 比对结果成功后 读取当前的模式
                if (readModeToHandleType  == R.id.change_mode_to_sleep){
                    //请求服务器是否检测完成 没完成提示未检测完成


                    return;
                }

                break;
            default:
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void handleCanChangeModeToSleep(NFCServiceStatusMsg nfcServiceStatusMsg){
        //TODO 获取服务器设备状态  检测完成 读取当前的模式 检测未完成 提示不能转换为slepp模式

    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//读取NFC的工作模式
    public void readNFCModeResultHandle(ReadModeRusult readModeRusult){
        switch (readModeToHandleType){
            case R.id.change_mode_to_sleep://转换为sleep的处理方式
                //todo 如果已经是sleep模式 提示已经是sleep模式并上报服务器 如果不是 下发指令切换为sleep模式

                break;

            case R.id.change_mode_to_work://转换为work的处理方式
                //todo 如果已经是work模式 提示已经是work模式并上报服务器 如果不是 下发指令切换为work模式

                break;

            default:

                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//写入RFID后的操作
    public void writeRfidResultHandle(WriteNFCResult writeNFCResult){

    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//更改为sleep完成后操作
    public void changeSleepResultHandle(ChangeSleepModeResult changeSleepModeResult){
        //TODO 提示已经转换为sleep并上报服务器

    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//更改为work完成后操作
    public void changeWorkResultHandle(ChangeWorkingModeResult changeWorkingModeResult){
        //TODO 提示已经转换为work并上报服务器

    }



    public void showToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public ProgressDialog setProgressDialogMsg(String msg){
        if (progressDialog == null)
            progressDialog = MyDialog.createProgressDialog(this,msg);
        progressDialog.setMessage(msg);
        return progressDialog;
    }
    public void setProgressDialogMsgAndShow(String msg){
        if (progressDialog == null)
            progressDialog = MyDialog.createProgressDialog(this,msg);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }
    public void dismssProgress(){
        if (progressDialog != null){
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter == null) {
            msgResultTextview.setText("设备不支持NFC！");
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            msgResultTextview.setText("请在系统设置中先启用NFC功能！");
            return;
        }


        //开启监听nfc设备
        if (nfcAdapter != null && this.nfcAdapter.isEnabled()) {

            this.nfcAdapter.enableForegroundDispatch(this, this.mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            Log.i(TAG, "onPause: 关闭监听nfc设备");
            nfcAdapter.disableForegroundDispatch(this);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        resolveIntent(intent);

    }
    protected void resolveIntent(Intent intent) {

        // 得到是否检测到TAG触发
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            setTextViewInitialText();

            // 处理该intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tag);
            readNfca(intent);

            // 获取标签id数组
            byte[] bytesId = tag.getId();
            String tagId = bytesToHexString(bytesId);

//            rfidTextview.setText("NFC ID: " + tagId);


        }
    }
    public void readNfca(Intent intent){
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcA nfcA = null;
        try
        {
            nfcA =  NfcA.get(tag);
            nfcA.connect();
            String atqa="";
            String str = "";
            for(byte tmpByte:nfcA.getAtqa())
            {
                atqa+=tmpByte;
            }
            str+="tag Atqa:"+bytesToHexString(nfcA.getAtqa())+"\n";//获取卡的atqa
            str+="tag SAK:"+nfcA.getSak()+"\n";//获取卡的sak
            str+="max len:"+nfcA.getMaxTransceiveLength()+"\n";//获取卡片能接收的最大指令长度


            byte[] READ = {
                    (byte) 0x30,//读
                    (byte) 0x04,//页数
            };
            byte[] result = nfcA.transceive(READ);
            if (result.length < 3){
                matchResultTextview.setText("读取RFID 有点问题");
                return;
            }
            int _rfid = result[0] + (result[1] << 8) + (result[2] << 16);
            rfidTextview.setText("RFID : " + _rfid);
            switch (readRfidToHandleType){
                case R.id.read_rfid_to_check:
                    if (result.length < 3) {
                        matchResultTextview.setText("读取RFID 有点问题");
                        return;
                    }
                    int rfid = result[0] + ( result[1] << 8 ) + ( result[2] << 16);
                    if (scanResult.isEmpty()){
                        //与扫描的结果作比较 给出提示信息
                        matchResultTextview.setText("扫描结果为空  请重新点击检查RFID按钮");
                        return;
                    }
                    try {
                        int scanRInt = Integer.parseInt(scanResult);
                        if (rfid == scanRInt){
                            matchResultTextview.setText("设备正常");
                            rfidTextview.setText("RFID : " + scanResult);
                        } else {
                            matchResultTextview.setText("设备的RFID与条形码的信息不匹配 请重新检查");
                        }
                    }catch (Exception e){
                        matchResultTextview.setText("条形码的扫描不正确");
                    }


                    break;
                case R.id.read_rfid_to_change_mode:
                    if (readModeToHandleType == R.id.change_mode_to_sleep){//变为睡眠模式
                        if ((result[4] & 0x01) == 0){//已经是睡眠模式了
                            msgResultTextview.setText("已经是睡眠模式了");
                            EventBus.getDefault().postSticky(new ChangeSleepModeResult());
                        }else {//不是睡眠模式  改为睡眠模式
                            byte[] change_mode_result = nfcA.transceive(SEND_CHANGE_TO_SLEEP);
                            msgResultTextview.setText("已经转换为睡眠模式了");
                            EventBus.getDefault().postSticky(new ChangeSleepModeResult());
                        }
                    }else if(readModeToHandleType == R.id.change_mode_to_work){//变为工作模式
                        if ((result[4] & 0x01) == 1){//已经是工作模式了
                            msgResultTextview.setText("已经是工作模式了");
                            EventBus.getDefault().postSticky(new ChangeWorkingModeResult());
                        }else {//不是工作模式 改为工作模式
                            byte[] change_mode_result = nfcA.transceive(SEND_CHANGE_TO_WORK);
                            try {
                                Thread.sleep(10);
                            }catch (Exception e){

                            }
                            byte[] fangchai_result = nfcA.transceive(SEND_FANG_CHAI);
                            msgResultTextview.setText("已经转换为工作模式了");
                            EventBus.getDefault().postSticky(new ChangeWorkingModeResult());
                        }
                    }
                    break;
                default:

                    break;
            }

//            if (isRead) {
//                matchResultTextview.setText("4区数据:" + bytesToHexString(result));
//                return;
//            }
//            nfcA.transceive(SEND_WILL_CHANGE_RFID);
//            Thread.sleep(10);
//            byte[] result_set = nfcA.transceive(SEND_WILL_CHANGE_RFID);
//            byte[] result = nfcA.transceive(READ);
//            matchResultTextview.setText("4区数据设置成功:" + bytesToHexString(result_set));
//            isRead = true;
        }catch(Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            matchResultTextview.setText("");
            showToast(e.toString());
        }finally{
            dismssProgress();
            if (nfcA != null){
                try {
                    nfcA.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //创建intent检测nfc
        mPendingIntent = PendingIntent
                .getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }


    /**
     * 数组转换成十六进制字符串
     *
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase() + " ");
        }
        return sb.toString();
    }

    public void changeDataAtFour(int ...data){
        for (int i = 0; i < data.length; i++) {
            SEND_CHANGE_RFID[i + 2] = (byte)(data[i] & 0xFF);
        }
    }

    public void setTextViewInitialText(){
        rfidTextview.setText("");
        matchResultTextview.setText("");
        msgResultTextview.setText("");
    }

}
