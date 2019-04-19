package com.ucast.tagmanager.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ucast.tagmanager.R;
import com.ucast.tagmanager.myview.MyInputDialog;


/**
 * Created by pj on 2017/7/10.
 */

public class MyDialog {

    public static void showDialog(Context context, String s) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setPositiveButton(context.getResources().getString(R
                .string.queding), null).create();
        alertDialog.setTitle(context.getResources().getString(R.string.tishi));
        alertDialog.setMessage(s);
        alertDialog.show();
    }

    public static void showToast(Context context, String s) {
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
    public static void showSnack(View view, String s) {
        Snackbar.make(view,s,Snackbar.LENGTH_SHORT).show();
    }

    public static ProgressDialog createProgressDialog(Context context, String s){
        ProgressDialog dialog2 = new ProgressDialog(context);
        dialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog2.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog2.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//            dialog2.setTitle(mContext.getResources().getString(R.string.tishi));
        dialog2.setMessage(s);
        return dialog2;
    }

 public static AlertDialog inputTitleDialog(Context context, String title, int icon, final View.OnClickListener listener) {
        final EditText inputServer = new EditText(context);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setIcon(icon).setView(inputServer);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null){
                    listener.onClick(inputServer);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        return dialog;
    }
 public static MyInputDialog createMyInputDialog(Context context) {
        MyInputDialog dialog = new MyInputDialog(context,R.style.MyDialog);
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        return dialog;
    }

}
