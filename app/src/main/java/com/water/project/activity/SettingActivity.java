package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import com.water.project.view.time.SlideDateTimeListener;
import com.water.project.view.time.SlideDateTimePicker;
import com.water.project.view.time.TimeUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 参数设置
 * Created by Administrator on 2018/9/1.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener{
    private EditText etCode,etPhone,etTanTou,etCEtime,etFEtime;
    private TextView etCStime,etFStime;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    private int SEND_TYPE;
    //设置统一编码和SIM的数据
    private String CODE_SIM_DATA;
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
//        sendData(BleContant.SEND_GET_CODE_PHONE,1); //发送蓝牙命令
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
        etCStime=(TextView) findViewById(R.id.et_as_cstime);
        etCEtime=(EditText)findViewById(R.id.et_as_cetime);
        etFStime=(TextView)findViewById(R.id.et_as_fstime);
        etFEtime=(EditText)findViewById(R.id.et_as_fetime);
        etCStime.setOnClickListener(this);
        etFStime.setOnClickListener(this);
        findViewById(R.id.tv_setting_one).setOnClickListener(this);
        findViewById(R.id.tv_setting_two).setOnClickListener(this);
        findViewById(R.id.tv_setting_three).setOnClickListener(this);
        findViewById(R.id.tv_setting_four).setOnClickListener(this);
        findViewById(R.id.tv_get_one).setOnClickListener(this);
        findViewById(R.id.tv_get_two).setOnClickListener(this);
        findViewById(R.id.tv_get_three).setOnClickListener(this);
        findViewById(R.id.tv_get_four).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }


    /**
     * 发送蓝牙命令
     * @param status
     */
    private void sendData(int status,int type){
        SEND_STATUS=status;
        SEND_TYPE=type;
        showProgress("发送数据中...");
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
        SendBleStr.sendBleData(status,type);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //设置统一编码和SIM
            case R.id.tv_setting_one:
                  final String code=etCode.getText().toString().trim();
                  final String sim=etPhone.getText().toString().trim();
                  if(TextUtils.isEmpty(code)){
                      showToastView("请输入统一编码！");
                  }else if(TextUtils.isEmpty(sim)){
                      showToastView("请输入SIM卡号！");
                  }else{
                      SendBleStr.sendSetCodeSim(code,sim,CODE_SIM_DATA);
                      sendData(BleContant.SET_CODE_PHONE,2);
                  }
                 break;
            //设置探头埋深
            case R.id.tv_setting_two:
                  final String tantou=etTanTou.getText().toString().trim();
                  if(TextUtils.isEmpty(tantou)){
                      showToastView("请输入探头埋深！");
                  }else if(!Util.getCharIndex(tantou)){
                      showToastView("小数点后面必须保留三位小数！");
                  }else{
                      SendBleStr.sendSetTanTou(tantou);
                      sendData(BleContant.SET_TANTOU,2);
                  }
                break;
            //选择采集时间
            case R.id.et_as_cstime:
                 TimeUtils.type=0;
                 new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .build()
                .show();
                 break;
            //设置采集频率
            case R.id.tv_setting_three:
                 final String date=etCStime.getText().toString().trim();
                 final String hour=etCEtime.getText().toString().trim();
                 if(TextUtils.isEmpty(date)){
                     showToastView("请输入采集起始时间！");
                 }else if(TextUtils.isEmpty(hour)){
                     showToastView("请输入采集间隔时间！");
                 }else{
                     SendBleStr.sendCaiJi(date,hour);
                     sendData(BleContant.SET_CAI_JI_PIN_LU,2);
                 }
                break;
            //选择发送起始时间
            case R.id.et_as_fstime:
                 TimeUtils.type=1;
                  new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
                  break;
            //设置发送频率
            case R.id.tv_setting_four:
                 final String startTime=etFStime.getText().toString().trim();
                 final String fHour=etFEtime.getText().toString().trim();
                 if(TextUtils.isEmpty(startTime)){
                     showToastView("请选择发送起始时间！");
                 }else if(TextUtils.isEmpty(fHour)){
                     showToastView("请输入发送间隔时间！");
                 }else{
                     SendBleStr.setFaSong(startTime,fHour);
                     sendData(BleContant.SET_FA_SONG,2);
                 }
                break;
            //读取统一编码和SIM
            case R.id.tv_get_one:
                 sendData(BleContant.SEND_GET_CODE_PHONE,2);
                 break;
            //读取探头埋深
            case R.id.tv_get_two:
                sendData(BleContant.SEND_GET_TANTOU,2);
                break;
            //读取采集频率
            case R.id.tv_get_three:
                sendData(BleContant.SEND_CAI_JI_PIN_LU,2);
                break;
            //读取发送频率
            case R.id.tv_get_four:
                sendData(BleContant.SEND_FA_SONG_PIN_LU,2);
                break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }
    }


    /**
     * 解析并显示回执的数据
     * @param data
     */
    private void showData(String data){
        String[] strings =null;
        switch (SEND_STATUS){
            //显示统一编码，SIM卡号
            case BleContant.SEND_GET_CODE_PHONE:
                 CODE_SIM_DATA=data;
                 strings=data.split(";");
                 etCode.setText(strings[1]);
                 etPhone.setText(strings[6]);
                 break;
            //显示探头埋深
            case BleContant.SEND_GET_TANTOU:
                  final String strTanTou=data.replace("GDLINER","");
                  etTanTou.setText(Util.setDouble(Double.parseDouble(strTanTou),3));
                 break;
            //显示采集频率
            case BleContant.SEND_CAI_JI_PIN_LU:
                 strings=data.split(",");
                 etCStime.setText(strings[0].replace("GDREADR",""));
                 etCEtime.setText((Integer.parseInt(strings[1])/60)+"");
                 break;
            //显示发送频率
            case BleContant.SEND_FA_SONG_PIN_LU:
                  strings=data.split(",");
                  etFStime.setText(strings[0].replace("GDSENDR",""));
                  etFEtime.setText(strings[1]);
                  break;
            default:
                break;
        }

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
        myIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE2);//接收到了回执的数据
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
                            sendData(SEND_STATUS,SEND_TYPE);
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
                            return;
                        }else{
                            isConnect=true;
                            dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialogView.dismiss();
                                    showProgress("蓝牙连接中...");
                                    mHandler.postDelayed(new Runnable() {
                                        public void run() {
                                            MainActivity.bleService.connect(SettingActivity.this.ble.getBleMac());
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
                     if(SEND_STATUS==BleContant.NOT_SEND_DATA){
                         showToastView("蓝牙连接成功！");
                     }else{
                         sendData(SEND_STATUS,SEND_TYPE);
                     }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    //解析并显示回执的数据
                    showData(data);
                    //继续发送命令
                    switch (SEND_STATUS){
                        case BleContant.SEND_GET_CODE_PHONE:
                            sendData(BleContant.SEND_GET_TANTOU,1);
                            break;
                        case BleContant.SEND_GET_TANTOU:
                            sendData(BleContant.SEND_CAI_JI_PIN_LU,1);
                            break;
                        case BleContant.SEND_CAI_JI_PIN_LU:
                            sendData(BleContant.SEND_FA_SONG_PIN_LU,1);
                            break;
                        case BleContant.SEND_FA_SONG_PIN_LU:
                             clearTask();
                             SEND_STATUS=BleContant.NOT_SEND_DATA;
                            break;
                        default:
                            clearTask();
                            break;
                    }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE2:
                     clearTask();
                     final String data2=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                     if(SEND_STATUS==BleContant.SEND_GET_CODE_PHONE || SEND_STATUS==BleContant.SEND_GET_TANTOU || SEND_STATUS==BleContant.SEND_CAI_JI_PIN_LU || SEND_STATUS==BleContant.SEND_FA_SONG_PIN_LU){
                         //解析并显示回执的数据
                         showData(data2);
                     }else{
                         dialogView = new DialogView(mContext, "参数设置成功！", "好的",null, new View.OnClickListener() {
                             public void onClick(View v) {
                                 dialogView.dismiss();
                             }
                         }, null);
                         dialogView.show();
                     }
                     SEND_STATUS=BleContant.NOT_SEND_DATA;
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


    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyyMMddHH");
    private SimpleDateFormat mFormatter2 = new SimpleDateFormat("HH");
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        public void onDateTimeSet(Date date) {
            if(TimeUtils.type==0){
                etCStime.setText(mFormatter.format(date));
            }else{
                etFStime.setText(mFormatter2.format(date));
            }
        }
        public void onDateTimeCancel() {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
