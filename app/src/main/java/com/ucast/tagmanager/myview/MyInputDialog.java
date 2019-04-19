package com.ucast.tagmanager.myview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.ucast.tagmanager.R;

/**
 * Created by pj on 2019/4/19.
 */
public class MyInputDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private EditText inputEt;
    private Button btn;

    private OnNextClicked onNextClicked;

    protected MyInputDialog(@NonNull Context context) {
        super(context);
    }

    public MyInputDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    private void initViews() {
        inputEt = findViewById(R.id.tuoche_number);
//        inputEt.setFocusable(true);
//        inputEt.setFocusableInTouchMode(true);
//        inputEt.requestFocus();


        btn = findViewById(R.id.next);
        btn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //提前设置一些样式
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        setContentView(R.layout.my_input_dialog);
        Display display = window.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth() * 4 / 5;
        window.setAttributes(lp);
        initViews();
    }

    public void setOnNextClicked(OnNextClicked onNextClicked){
        this.onNextClicked = onNextClicked;
    }

    @Override
    public void onClick(View v) {
        if (onNextClicked != null)
            onNextClicked.onNextClickedListener(inputEt);
    }

    public interface OnNextClicked{
        void onNextClickedListener(EditText input);
    }
}
