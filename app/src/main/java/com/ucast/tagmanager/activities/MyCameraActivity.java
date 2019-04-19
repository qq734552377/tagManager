package com.ucast.tagmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ucast.tagmanager.R;
import com.ucast.tagmanager.eventBusMsg.TakePhotoOK;
import com.ucast.tagmanager.eventBusMsg.TakePhotoPath;
import com.ucast.tagmanager.tools.FullScreenHelper;
import com.ucast.tagmanager.tools.InfraredCameraInterface;
import com.ucast.tagmanager.tools.MyTools;
import com.ucast.tagmanager.view.mysaomiao.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MyCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    public static boolean ISPORTRAIT = true;
    public final static String RESULT = "result";
    private String imagePath = "";
    SurfaceView sf;
    SurfaceHolder surfaceHolder;

    RelativeLayout rl_camera;
    RelativeLayout rl_image;

    ImageView showImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);

        EventBus.getDefault().register(this);
        initViews();
    }

    private void initViews() {
        sf = findViewById(R.id.sf);
        surfaceHolder = sf.getHolder();
        surfaceHolder.addCallback(this);
        findViewById(R.id.pahzhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfraredCameraInterface.getInstance().takePhoto();
            }
        });

        rl_camera = findViewById(R.id.rl_camera);
        rl_image = findViewById(R.id.rl_image);

        findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePath = "";
                setRl_cameraShow(true);
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePath.isEmpty())
                    return;
                EventBus.getDefault().postSticky(new TakePhotoPath(imagePath));
                finish();
            }
        });

        showImage = findViewById(R.id.paizhao_show);
    }

    public void setRl_cameraShow(boolean isShow){
        if (rl_camera == null)
            return;
        if (rl_image == null)
            return;
        if (isShow){
            rl_camera.setVisibility(View.VISIBLE);
            rl_image.setVisibility(View.GONE);
        }else {
            rl_camera.setVisibility(View.GONE);
            rl_image.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!InfraredCameraInterface.getInstance().isPreviewing()) {
            InfraredCameraInterface.getInstance().doOpenCamera();
            InfraredCameraInterface.getInstance().doStartPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        super.onResume();
//        InfraredCameraInterface.getInstance().doOpenCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InfraredCameraInterface.getInstance().doStopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)//读取到RFID后的操作
    public void takeOk(TakePhotoOK takePhotoOK){
        imagePath = takePhotoOK.getPath();
        setRl_cameraShow(false);
        showImage.setImageBitmap(MyTools.rotateBitmap(BitmapFactory.decodeFile(imagePath),90));
    }

    public void closeMyself(int type,String msg){
        Intent intent = new Intent();
        intent.putExtra(RESULT, msg);
        setResult(type, intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
