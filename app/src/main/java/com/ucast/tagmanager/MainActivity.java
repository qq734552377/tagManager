package com.ucast.tagmanager;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ucast.tagmanager.activities.LoginActivity;
import com.ucast.tagmanager.activities.MyCameraActivity;
import com.ucast.tagmanager.entity.OperateStatus;
import com.ucast.tagmanager.eventBusMsg.AllThingOk;
import com.ucast.tagmanager.eventBusMsg.CanReadDianYaEvent;
import com.ucast.tagmanager.eventBusMsg.ChangeSleepModeResult;
import com.ucast.tagmanager.eventBusMsg.ChangeWorkingModeResult;
import com.ucast.tagmanager.eventBusMsg.GetServiceCanActiveMsg;
import com.ucast.tagmanager.eventBusMsg.NFCServiceStatusMsg;
import com.ucast.tagmanager.eventBusMsg.ReadModeRusult;
import com.ucast.tagmanager.eventBusMsg.ReadNFCRFIDMsg;
import com.ucast.tagmanager.eventBusMsg.TakePhotoPath;
import com.ucast.tagmanager.eventBusMsg.WriteNFCResult;
import com.ucast.tagmanager.exception.ExceptionApplication;
import com.ucast.tagmanager.myview.MyInputDialog;
import com.ucast.tagmanager.tools.HttpRequest;
import com.ucast.tagmanager.tools.MyDialog;
import com.ucast.tagmanager.tools.MyTools;
import com.ucast.tagmanager.tools.SavePasswd;
import com.ucast.tagmanager.view.mysaomiao.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.HashMap;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public String scanResult = "";
    public String tuoBancheId = "";
    public ProgressDialog progressDialog = null;
    private MyInputDialog inputTuobancheIdDialog = null;
    public int readRfidToHandleType = 0;
    public int readModeToHandleType = 0;
    private boolean isUploading = false;
    private boolean isCanReadDianYa = false;
    private int readDianYaTimes = 0;
    private int readDianYaMaxNumber = 5;
    private float MINDIANYA = 3.4f;
    private float MAXDIANYA = 3.85f;

    private OperateStatus status;

    public static final int CAMERA = 777;
    private String  imagePath = "";

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
    byte[] SEND_DIAN_YA_FIRST = {
            (byte) 0xA2,//写
            (byte) 0x06,//页数
            (byte) 0x61,//a
            (byte) 0x62,//b
            (byte) 0x63,//c
            (byte) 0x64 //d
    };
    byte[] SEND_DIAN_YA_SECOND = {
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
        readRfidToHandleType = R.id.read_rfid_to_change_mode;
        readModeToHandleType = R.id.change_mode_to_sleep;
        readDianYaTimes = 0;
//        checkRfidToChangeMode();
        setProgressDialogMsgAndShow(getString(R.string.nfc_to_sleep));

    }
    @Event(R.id.change_to_work)
    private void change_to_work(View v){
        initTextViewMsg();
        initPathAndTuocheID();
        readModeToHandleType = R.id.change_mode_to_work;
        dismssProgress();
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

        switch (resultCode) {
            case 0://侧面摄像头
                scanResult = data.getStringExtra(CaptureActivity.RESULT);
//                scanResult = "03343107";
                //todo 判断扫描结果长度  打开系统相机
                barCodeTextview.setText(getString(R.string.barcode) + ":" + scanResult);
                if(readRfidToHandleType == R.id.read_rfid_to_check){
                    setProgressDialogMsgAndShow(getString(R.string.nfc_to_check));
                    return;
                }
                if (scanResult.length() == 8){
//                    startCamera();
                    if (readModeToHandleType == R.id.change_mode_to_work)
                        showTuobancheDialog();
                }else{
                    MyDialog.showToast(MainActivity.this,getString(R.string.scancode_length_wrong));
                    msgResultTextview.setText(getString(R.string.scancode_length_wrong));
                }
                break;
            case 1://前面摄像头
                scanResult = data.getStringExtra(CaptureActivity.RESULT);
                barCodeTextview.setText(getString(R.string.barcode) + ":" + scanResult);
                break;
        }

    }

    public void startCamera(){
        try {
            Intent intent = new Intent(this, MyCameraActivity.class);
            startActivity(intent);
        }catch (Exception e){

        }
    }

    public void checkRfid(){
        initTextViewMsg();
        initPathAndTuocheID();
        dismssProgress();
        readRfidToHandleType = R.id.read_rfid_to_check;
        readDianYaTimes = 0;
        startAc(0);
    }
    public void checkRfidToChangeMode(){
        readRfidToHandleType = R.id.read_rfid_to_change_mode;
        readDianYaTimes = 0;
        startAc(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//读取到RFID后的操作
    public void takePhotoPath(TakePhotoPath takePhotoPath){
        imagePath = takePhotoPath.getPath();
        setProgressDialogMsgAndShow(getString(R.string.nfc_to_work));
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//读取到RFID后的操作
    public void getServiceCanActive(GetServiceCanActiveMsg msg){
        isUploading = false;
        if (msg.isCanActived()){
            status = OperateStatus.GETCANACTIVED;
        }else{
            if (msg.getInfo().equals(getString(R.string.token_error))){
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return;
            }
            initPathAndTuocheID();
            dismssProgress();
        }
        msgResultTextview.setText(msg.getInfo());
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//读取到RFID后的操作
    public void allThingsOk(final AllThingOk msg){
        isUploading = false;
        dismssProgress();
        if (msg.isStatus()) {
            initPathAndTuocheID();
            msgResultTextview.setText(msg.getInfo());
        }else{
            if (msg.getInfo().equals(getString(R.string.token_error))){
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return;
            }
            if (msg.getInfo().equals(getString(R.string.timeout))){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(ExceptionApplication.getInstance().getString(R.string.tishi));
                builder.setMessage(ExceptionApplication.getInstance().getString(R.string.tishi_timeout));
                builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeWorkResultHandle(new ChangeWorkingModeResult(true,msg.getBarCode()));
                    }
                });
                builder.setNegativeButton("忽略", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyTools.writeToActiveCsv(msg.getBarCode() + "," + OperateStatus.ACTIVATESTR);
                        msgResultTextview.setText(getString(R.string.tishi_hulve));
                    }
                });
                builder.show();
            }
            msgResultTextview.setText(msg.getInfo());
            initPathAndTuocheID();
        }


    }

    public void isCanReadDianYa(CanReadDianYaEvent event){
        if (event.isCanReadDianYa()){
            isCanReadDianYa = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//读取到RFID后的操作
    public void readRfidResultHandle(ReadNFCRFIDMsg readNFCMsg){
        // TODO 读取rfid 检测RFID与扫描得到的条形码比对 不相同直接返回
        msgResultTextview.setText(readNFCMsg.getMsg());
//        dismssProgress();
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
        // 提示已经转换为work并上报服务器
        if(changeWorkingModeResult.getRfid().isEmpty())
            return;
        HttpRequest.sendDeviceModeStatus(changeWorkingModeResult.getRfid(),OperateStatus.ACTIVATESTR);
    }



    public void showToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void showTuobancheDialog(){
        if (inputTuobancheIdDialog == null) {
            inputTuobancheIdDialog = MyDialog.createMyInputDialog(this);
            inputTuobancheIdDialog.setOnNextClicked(new MyInputDialog.OnNextClicked() {
                @Override
                public void onNextClickedListener(EditText input) {
                    String str = input.getText().toString().trim();
                    if (str.isEmpty()){
                        showToast(getString(R.string.no_tuoche_id));
                    }else {
                        tuoBancheId = str;
                        input.setText("");
                        startCamera();
                        inputTuobancheIdDialog.dismiss();
                    }
                }
            });
        }
        inputTuobancheIdDialog.show();
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

    public boolean isProgressShow(){
        if (progressDialog == null)
            return false;
        return progressDialog.isShowing();
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
            msgResultTextview.setText(getString(R.string.not_surport_nfc));
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            msgResultTextview.setText(getString(R.string.please_open_nfc));
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
        if (!isProgressShow()) {
            showToast(getString(R.string.please_active_rfid_by_rule));
            return;
        }
        if (isUploading){
            msgResultTextview.setText(getString(R.string.upload_data_));
            return;
        }
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

            nfcA.transceive(SEND_DIAN_YA_FIRST);

            try {
                Thread.sleep(80);
            }catch (Exception e){

            }

            byte[] READ = {
                    (byte) 0x30,//读
                    (byte) 0x04,//页数
            };
            byte[] result = nfcA.transceive(READ);
            int deviceStatus = (result[4] & 0x01);
            int dianya = (result[8] & 0xff) + ((result[9] & 0xff)<<8);
            float dianyaF = (float)dianya * 3.6f * 7f /4096f;
            if (dianyaF <= 0){
                if (readDianYaTimes <= readDianYaMaxNumber) {
                    msgResultTextview.setText(getString(R.string.dianya_show) + dianyaF);
                    readDianYaTimes++;
                    return;
                }
            }
            readDianYaTimes = 0;
            if (dianyaF <= MINDIANYA){
                msgResultTextview.setText(getString(R.string.dianya_low)+ getString(R.string.dianya_show) + dianyaF);
                dismssProgress();
                return;
            }
            if (dianyaF > MAXDIANYA){
                msgResultTextview.setText(getString(R.string.dianya_high)+ getString(R.string.dianya_show) + dianyaF);
                dismssProgress();
                return;
            }

            if (result.length < 3){
                matchResultTextview.setText(getString(R.string.read_rfid_error));
                dismssProgress();
                return;
            }
            int _rfid = result[0] & 0xff  + (result[1] & 0xff << 8) + (result[2] & 0xff << 16);
            barCodeTextview.setText(getString(R.string.barcode) + ":" + scanResult);
            rfidTextview.setText(getString(R.string.rfid_) + String.format("%08d",_rfid));
            switch (readRfidToHandleType){
                case R.id.read_rfid_to_check:
                    if (result.length < 3) {
                        matchResultTextview.setText(getString(R.string.read_rfid_error));
                        dismssProgress();
                        return;
                    }
                    if (scanResult.isEmpty()){
                        //与扫描的结果作比较 给出提示信息
                        matchResultTextview.setText(getString(R.string.scancode_empty));
                        dismssProgress();
                        return;
                    }
                    try {
                        int scanRInt = Integer.parseInt(scanResult);
                        if (_rfid == scanRInt){
                            matchResultTextview.setText(getString(R.string.device_ok));
                            rfidTextview.setText(getString(R.string.rfid_) + scanResult);
                            if (deviceStatus == 1){
                                msgResultTextview.setText(getString(R.string.workmode_now_) + " \n" + getString(R.string.dianya_show) + dianyaF);
                            }else{
                                msgResultTextview.setText(getString(R.string.sleepmode_now_) + " \n" + getString(R.string.dianya_show) + dianyaF);
                            }
                            dismssProgress();
                        } else {
                            matchResultTextview.setText(getString(R.string.rfid_not_match_scancode));
                            dismssProgress();
                        }
                    }catch (Exception e){
                        matchResultTextview.setText(getString(R.string.scan_code_pos_wrong));
                        dismssProgress();
                        return;
                    }


                    break;
                case R.id.read_rfid_to_change_mode:


                    if (readModeToHandleType == R.id.change_mode_to_sleep){//变为睡眠模式
                        if ((result[4] & 0x01) == 0){//已经是睡眠模式了
                            msgResultTextview.setText(getString(R.string.sleepmode_now));
                            EventBus.getDefault().postSticky(new ChangeSleepModeResult());
                            dismssProgress();
                        }else {//不是睡眠模式  改为睡眠模式
                            byte[] change_mode_result = nfcA.transceive(SEND_CHANGE_TO_SLEEP);
                            msgResultTextview.setText(getString(R.string.change_sleepmode_success));
                            EventBus.getDefault().postSticky(new ChangeSleepModeResult());
                            dismssProgress();
                        }
                    }else if(readModeToHandleType == R.id.change_mode_to_work){//变为工作模式
                        if (imagePath.isEmpty()){
                            msgResultTextview.setText(getString(R.string.no_photo_msg));
                            dismssProgress();
                            return;
                        }
                        if (tuoBancheId.isEmpty()){
                            msgResultTextview.setText(getString(R.string.no_tuoche_msg));
                            dismssProgress();
                            return;
                        }
                        if (scanResult.isEmpty()){
                            //与扫描的结果作比较 给出提示信息
                            matchResultTextview.setText(getString(R.string.scancode_empty));
                            dismssProgress();
                            return;
                        }
                        try {
                            int scanRInt = Integer.parseInt(scanResult);
                            if (_rfid == scanRInt){
                                matchResultTextview.setText(getString(R.string.device_ok));
                                rfidTextview.setText(getString(R.string.rfid_) + scanResult);
                            } else {
                                matchResultTextview.setText(getString(R.string.rfid_not_match_scancode));
                                dismssProgress();
                                return;
                            }
                        }catch (Exception e){
                            matchResultTextview.setText(getString(R.string.scan_code_pos_wrong));
                            dismssProgress();
                            return;
                        }
                        if (deviceStatus == 1){
                            status = OperateStatus.ACTIVED;
                        }
                        int fangchaiStatus = (result[4] >> 1) & 0x01;
                        if (status.getLevel() < OperateStatus.GETCANACTIVED.getLevel()){
                           if (status == OperateStatus.SLEEP){
                                status = OperateStatus.IFACTIVED;
                                isUploading = true;
                                HttpRequest.sendWillChangeDeviceModeSatus(scanResult,tuoBancheId,OperateStatus.WAITACTIVATESTR,imagePath);
                                return;
                            }
                        }
                        if (deviceStatus == 1){//已经是工作模式了
                            msgResultTextview.setText(getString(R.string.workmode_now));
                            //设置防拆
                            byte[] fangchai_result = nfcA.transceive(SEND_FANG_CHAI);
                            dismssProgress();
                            initPathAndTuocheID();
                        }else {//不是工作模式 改为工作模式
                            byte[] change_mode_result = nfcA.transceive(SEND_CHANGE_TO_WORK);
                            try {
                                Thread.sleep(80);
                            }catch (Exception e){

                            }
                            //设置防拆
                            byte[] fangchai_result = nfcA.transceive(SEND_FANG_CHAI);
                            msgResultTextview.setText(getString(R.string.change_workmode_success));
                            MyTools.writeSimpleLogWithTime(SavePasswd.getInstace().get(MyTools.LOGIN_ID) + "操作，  " + scanResult + "  激活成功");
                            isUploading = true;
                            changeWorkResultHandle(new ChangeWorkingModeResult(true,String.format("%08d",_rfid)));
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
//            dismssProgress();
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
        barCodeTextview.setText("");
        rfidTextview.setText("");
        matchResultTextview.setText("");
        msgResultTextview.setText("");
    }

    public void initPathAndTuocheID(){
        status = OperateStatus.SLEEP;
        imagePath = "";
        tuoBancheId = "";
    }

}
