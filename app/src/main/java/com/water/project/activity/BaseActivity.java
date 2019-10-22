package com.water.project.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.water.project.R;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.view.CycleWheelView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/7/2 0002.
 */

public class BaseActivity extends FragmentActivity {

    public PopupWindow mPopuwindow;
    public Dialog baseDialog;
    protected Context mContext = this;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
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

//
//
//    protected void bottomPopupWindow(int x, int y, View view) {
//        mPopuwindow = new PopupWindow(view,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        ColorDrawable cd = new ColorDrawable(Color.argb(0, 0, 0, 0));
//        mPopuwindow.setBackgroundDrawable(cd);
//        mPopuwindow.setOutsideTouchable(true);
//        mPopuwindow.setFocusable(true);
//        mPopuwindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        mPopuwindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        mPopuwindow.showAsDropDown(getWindow().getDecorView(),x,y,Gravity.BOTTOM);
//    }


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
     * 滚动选择器
     * @param list
     * @param tv
     */
    private String message;
    protected void wheel(List<String> list, final TextView tv,final int type){
        View view= LayoutInflater.from(this).inflate(R.layout.wheel,null);
        TextView tvTitle=(TextView)view.findViewById(R.id.tv_wh_title);
        if(type==1){
            tvTitle.setText("请选择几小时");
        }else{
            tvTitle.setText("请选择几点");
        }
        CycleWheelView cycleWheelView=(CycleWheelView)view.findViewById(R.id.cycleWheelView);
        cycleWheelView.setLabels(list);
        try {
            cycleWheelView.setWheelSize(5);
        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
        cycleWheelView.setCycleEnable(false);
        cycleWheelView.setSelection(0);
        cycleWheelView.setAlphaGradual(0.5f);
        cycleWheelView.setDivider(Color.parseColor("#abcdef"),1);
        cycleWheelView.setSolid(Color.WHITE,Color.WHITE);
        cycleWheelView.setLabelColor(Color.GRAY);
        cycleWheelView.setLabelSelectColor(Color.BLACK);
        cycleWheelView.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                message=label;
            }
        });
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeDialog();
                message=message.replace("小时","");
                tv.setText(message);
            }
        });
        cycleWheelView.setSelection(5);
       dialogPop(view,true);
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

    /**
     * 弹出软键盘
     *
     * @param et
     */
    public void openKey(EditText et) {
        InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et, 0);
    }
}
