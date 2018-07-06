package com.water.project.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.water.project.R;

/**
 * Created by Administrator on 2018/7/2 0002.
 */

public class BaseActivity extends Activity {

    ProgressDialog progressDialog = null;
    public PopupWindow mPopuwindow;
    public Dialog baseDialog;
    protected Context mContext = this;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * 页面跳转
     * @param cls
     */
    protected void setClass(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        startActivity(intent);
    }

    /**
     * 自定义toast
     *
     * @param message
     */
    public void showToastView(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }


    /**
     * 取消进度条
     */
    public void clearTask() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    /**
     * 显示进度条
     * @param msg
     */
    public void showProgress(String msg) {
        //如果已经存在并且在显示中就不处理
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    protected void bottomPopupWindow(int x, int y, View view,View parent) {
        mPopuwindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ColorDrawable cd = new ColorDrawable(Color.argb(0, 0, 0, 0));
        mPopuwindow.setBackgroundDrawable(cd);
        mPopuwindow.setOutsideTouchable(true);
        mPopuwindow.setFocusable(true);
        mPopuwindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopuwindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopuwindow.showAsDropDown(parent,x,y,Gravity.BOTTOM);
    }


    /**
     * dialog弹框
     *
     * @param view
     */
    public Dialog dialogPop(View view, boolean b) {
        baseDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        baseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        baseDialog.setTitle(null);
        baseDialog.setCancelable(b);
        baseDialog.setContentView(view);
        Window window = baseDialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        baseDialog.show();
        return baseDialog;
    }
    public void closeDialog() {
        if (baseDialog != null) {
            baseDialog.dismiss();
        }
    }


    /**
     * 确保系统字体大小不会影响app中字体大小
     *
     * @return
     */
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
