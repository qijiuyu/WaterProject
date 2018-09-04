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
import android.widget.ImageView;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

public class NetSettingActivity extends BaseActivity implements View.OnClickListener{

    private ImageView img1,img2,img3;
    private EditText etAddress1,etAddress2,etAddress3,etApn,etIp1,etIp2,etIp3,etPort1,etPort2,etPort3;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_net);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_1fc37f);
        initView();
        sendData(BleContant.SEND_GET_CODE_PHONE);
    }


    private void initView(){
        TextView textView=(TextView)findViewById(R.id.tv_head);
        textView.setText("网络连接设置");
        img1=(ImageView)findViewById(R.id.img_an1);
        img2=(ImageView)findViewById(R.id.img_an2);
        img3=(ImageView)findViewById(R.id.img_an3);
        etAddress1=(EditText)findViewById(R.id.tv_address1);
        etAddress2=(EditText)findViewById(R.id.tv_address2);
        etAddress3=(EditText)findViewById(R.id.tv_address3);
        etApn=(EditText)findViewById(R.id.et_apn);
        etIp1=(EditText)findViewById(R.id.et_ip1);
        etIp2=(EditText)findViewById(R.id.et_ip2);
        etIp3=(EditText)findViewById(R.id.et_ip3);
        etPort1=(EditText)findViewById(R.id.et_port1);
        etPort2=(EditText)findViewById(R.id.et_port2);
        etPort3=(EditText)findViewById(R.id.et_port3);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);

    }


    /**
     * 发送蓝牙命令
     */
    private void sendData(int status){
        SEND_STATUS=status;
        showProgress("数据查询中...");
        //如果蓝牙连接断开，就扫描重连
        if(MainActivity.bleService.connectionState==MainActivity.bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                showProgress("扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(BleContant.SEND_REAL_TIME_DATA,1);
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
        myIntentFilter.addAction(BleService.ACTION_INTERACTION_TIMEOUT);//发送命令超时
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private boolean isConnect = true;
    private Ble ble;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    clearTask();
                    dialogView = new DialogView(mContext, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
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
                                    MainActivity.bleService.connect(NetSettingActivity.this.ble.getBleMac());
                                }
                            },100);
                            return;
                        }else{
                            isConnect=true;
                            dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialogView.dismiss();
                                    showProgress("蓝牙连接中...");
                                    mHandler.postDelayed(new Runnable() {
                                        public void run() {
                                            MainActivity.bleService.connect(NetSettingActivity.this.ble.getBleMac());
                                        }
                                    },100);
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
                    sendData(SEND_STATUS);
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    //解析并显示回执的数据
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    clearTask();
                    showToastView("接收数据超时！");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_an1:
                 if(null==v.getTag()){
                     return;
                 }
                 if(v.getTag().toString().equals("1")){
                     v.setTag(0);
                     img1.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
                 }else{
                     v.setTag(1);
                     img1.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
                 }
                 break;
            case R.id.img_an2:
                if(null==v.getTag()){
                    return;
                }
                if(v.getTag().toString().equals("1")){
                    v.setTag(0);
                    img2.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
                }else{
                    v.setTag(1);
                    img2.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
                }
                break;
            case R.id.img_an3:
                if(null==v.getTag()){
                    return;
                }
                if(v.getTag().toString().equals("1")){
                    v.setTag(0);
                    img3.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
                }else{
                    v.setTag(1);
                    img3.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
                }
                break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }
    }
}
