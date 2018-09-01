package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

/**
 * 参数设置
 * Created by Administrator on 2018/9/1.
 */

public class SettingActivity extends BaseActivity {
    private EditText etCode,etPhone,etTanTou,etCStime,etCEtime,etFStime,etFEtime;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_setting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_1fc37f);
        initView();
        register();//注册广播
        sendData(BleContant.SEND_GET_CODE_PHONE); //发送蓝牙命令
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("参数设置");
        etCode=(EditText)findViewById(R.id.et_as_code);
        etPhone=(EditText)findViewById(R.id.et_as_phone);
        etTanTou=(EditText)findViewById(R.id.et_as_tantou);
        etCStime=(EditText)findViewById(R.id.et_as_cstime);
        etCEtime=(EditText)findViewById(R.id.et_as_cetime);
        etFStime=(EditText)findViewById(R.id.et_as_fstime);
        etFEtime=(EditText)findViewById(R.id.et_as_fetime);
    }


    /**
     * 发送蓝牙命令
     * @param status
     */
    private void sendData(int status){
        SEND_STATUS=status;
        showProgress("发送数据中...");
        //如果蓝牙连接断开，就扫描重连
        if(MainActivity.bleService.connectionState==MainActivity.bleService.STATE_DISCONNECTED){
            //保存下发的蓝牙指令
            MyApplication.spUtil.addInt(SPUtil.SEND_BLE_CODE,status);
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
        }
        SendBleStr.sendBleData(status);
    }


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_NO_DISCOVERY_BLE);//扫描不到指定蓝牙设备
        myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);//蓝牙断开连接
        myIntentFilter.addAction(BleService.ACTION_ENABLE_NOTIFICATION_SUCCES);//蓝牙初始化通道成功
        myIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);//接收到了回执的数据
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    private boolean isConnect = true;
    private Ble ble;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    dialogView = new DialogView(mContext, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            final int status=MyApplication.spUtil.getInteger(SPUtil.SEND_BLE_CODE);
                            sendData(status);
                        }
                    }, null);
                    dialogView.show();
                      break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    final int status=intent.getIntExtra("status",0);
                    if(status!=0){
                        ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                        if (isConnect) {
                            isConnect=false;
                            LogUtils.e("重新连接一次蓝牙!");
                            mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    MainActivity.bleService.connect(SettingActivity.this.ble.getBleMac());
                                }
                            },100);
                        }else{
                            isConnect=true;
                            dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialogView.dismiss();
                                    showProgress("蓝牙连接中...");
                                    MainActivity.bleService.connect(SettingActivity.this.ble.getBleMac());
                                }
                            }, null);
                            dialogView.show();
                        }
                    }
                    clearTask();
                    showToastView("蓝牙连接断开！");
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                     final int status2=MyApplication.spUtil.getInteger(SPUtil.SEND_BLE_CODE);
                     sendData(status2);
                     break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                     clearTask();
                     final String data=intent.getStringExtra("ACTION_EXTRA_DATA");
                     //解析并显示回执的数据
                     showData(data);
                     //继续发送命令
                     switch (SEND_STATUS){
                         case BleContant.SEND_GET_CODE_PHONE:
                              sendData(BleContant.SEND_GET_TANTOU);
                              break;
                     }
                     break;
            }
        }
    };


    /**
     * 解析并显示回执的数据
     * @param data
     */
    private void showData(String data){
        switch (SEND_STATUS){
            case BleContant.SEND_GET_CODE_PHONE:
                break;
        }

    }
}
