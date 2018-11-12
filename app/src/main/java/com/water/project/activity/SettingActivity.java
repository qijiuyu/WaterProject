package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
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
    private EditText etCode,etPhone,etTanTou;
    private TextView etCStime,etFStime,etCEtime,etFEtime;
    private ImageView imgClear1,imgClear2,imgClear3;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    private int SEND_TYPE;
    //设置统一编码和SIM的数据
    private String CODE_SIM_DATA;
    private SimpleDateFormat mFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
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
        sendData(BleContant.SEND_GET_CODE_PHONE,1); //发送蓝牙命令
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
        etCEtime=(TextView)findViewById(R.id.tv_as_cetime);
        etFStime=(TextView)findViewById(R.id.et_as_fstime);
        etFEtime=(TextView)findViewById(R.id.et_as_fetime);
        imgClear1=(ImageView)findViewById(R.id.img_clear1);
        imgClear2=(ImageView)findViewById(R.id.img_clear2);
        imgClear3=(ImageView)findViewById(R.id.img_clear3);
        etCStime.setOnClickListener(this);
        etFStime.setOnClickListener(this);
        etCEtime.setOnClickListener(this);
        etFEtime.setOnClickListener(this);
        imgClear1.setOnClickListener(this);
        imgClear2.setOnClickListener(this);
        imgClear3.setOnClickListener(this);
        findViewById(R.id.tv_setting_one).setOnClickListener(this);
        findViewById(R.id.tv_setting_two).setOnClickListener(this);
        findViewById(R.id.tv_setting_three).setOnClickListener(this);
        findViewById(R.id.tv_setting_four).setOnClickListener(this);
        findViewById(R.id.tv_get_one).setOnClickListener(this);
        findViewById(R.id.tv_get_two).setOnClickListener(this);
        findViewById(R.id.tv_get_three).setOnClickListener(this);
        findViewById(R.id.tv_get_four).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClear1.setVisibility(View.VISIBLE);
                }else{
                    imgClear1.setVisibility(View.GONE);
                    etCode.setTextColor(getResources().getColor(R.color.color_1fc37f));
                }
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClear2.setVisibility(View.VISIBLE);
                }else{
                    imgClear2.setVisibility(View.GONE);
                    etPhone.setTextColor(getResources().getColor(R.color.color_1fc37f));
                }
            }
        });
        etTanTou.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    imgClear3.setVisibility(View.VISIBLE);
                }else{
                    imgClear3.setVisibility(View.GONE);
                    etTanTou.setTextColor(getResources().getColor(R.color.color_1fc37f));
                }
            }
        });
    }


    /**
     * 发送蓝牙命令
     * @param status
     */
    private void sendData(int status,int type){
        //判断蓝牙是否打开
        if(!BleUtils.isEnabled(SettingActivity.this,MainActivity.mBtAdapter)){
            return;
        }
        SEND_STATUS=status;
        SEND_TYPE=type;
        switch (SEND_STATUS){
            case BleContant.SEND_GET_CODE_PHONE:
            case BleContant.SEND_GET_TANTOU:
            case BleContant.SEND_CAI_JI_PIN_LU:
            case BleContant.SEND_FA_SONG_PIN_LU:
                 showProgress("正在读取参数设置...");
                 break;
             default:
                 showProgress("正在设置参数信息...");
                 break;
        }
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
                  }else if(code.length()<10 || code.length()==11){
                      etCode.setTextColor(getResources().getColor(R.color.color_EC191B));
                      showToastView("统一编码的长度只能是10位或者12位！");
                  }else if(TextUtils.isEmpty(sim)){
                      etCode.setTextColor(getResources().getColor(R.color.color_1fc37f));
                      showToastView("请输入SIM卡号！");
                  }else if(sim.length()<11 || sim.length()==12){
                      etCode.setTextColor(getResources().getColor(R.color.color_1fc37f));
                      etPhone.setTextColor(getResources().getColor(R.color.color_EC191B));
                      showToastView("SIM卡号位数错误！");
                  }else{
                      etCode.setTextColor(getResources().getColor(R.color.color_1fc37f));
                      etPhone.setTextColor(getResources().getColor(R.color.color_1fc37f));
                      SendBleStr.sendSetCodeSim(code,sim,CODE_SIM_DATA);
                      sendData(BleContant.SET_CODE_PHONE,2);
                  }
                 break;
            //设置探头埋深
            case R.id.tv_setting_two:
                  final String tantou=etTanTou.getText().toString().trim();
                  final int qIndex=tantou.indexOf(".");
                  final int hIndex=tantou.length()-qIndex-1;
                  etTanTou.setTextColor(getResources().getColor(R.color.color_EC191B));
                  if(TextUtils.isEmpty(tantou)){
                      showToastView("请输入探头埋深！");
                  }else if(tantou.indexOf(".")==-1 && tantou.length()>4){
                      showToastView("探头埋深最多只能输入4位整数！");
                  }else if(qIndex>4){
                      showToastView("探头埋深的小数点前面最多只能是4位数");
                  }else if(hIndex>3){
                      showToastView("探头埋深的小数点后面最多只能是3位数");
                  }else{
                      etTanTou.setTextColor(getResources().getColor(R.color.color_1fc37f));
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
            //选择采集间隔时间
            case R.id.tv_as_cetime:
                 wheel(Util.getHourList(),etCEtime,1);
                 break;
            //设置采集频率
            case R.id.tv_setting_three:
                 final String date=etCStime.getText().toString().trim();
                 final String hour=etCEtime.getText().toString().trim();
                 if(TextUtils.isEmpty(date)){
                     showToastView("请选择采集起始时间！");
                 }else if(TextUtils.isEmpty(hour)){
                     showToastView("请选择采集间隔时间！");
                 }else{
                     dialogView = new DialogView(mContext, "采集起始时间和采集间隔时间一旦修改，RTU内部存储数据将全部清空!","确定", "取消", new View.OnClickListener() {
                         public void onClick(View v) {
                             dialogView.dismiss();
                             SendBleStr.sendCaiJi(date.substring(0, date.length()-2).replace("-","").replace(" ","").replace(":",""),hour);
                             sendData(BleContant.SET_CAI_JI_PIN_LU,2);
                         }
                     }, null);
                     dialogView.show();
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
            //选择发送间隔小时
            case R.id.et_as_fetime:
                 wheel(Util.getHourList(),etFEtime,1);
                 break;
            //设置发送频率
            case R.id.tv_setting_four:
                 final String startTime=etFStime.getText().toString().trim();
                 final String fHour=etFEtime.getText().toString().trim();
                 if(TextUtils.isEmpty(startTime)){
                     showToastView("请选择发送起始时间！");
                 }else if(TextUtils.isEmpty(fHour)){
                     showToastView("请选择发送间隔时间！");
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
            case R.id.img_clear1:
                 etCode.setText(null);
                 imgClear1.setVisibility(View.GONE);
                 break;
            case R.id.img_clear2:
                etPhone.setText(null);
                imgClear2.setVisibility(View.GONE);
                break;
            case R.id.img_clear3:
                etTanTou.setText(null);
                imgClear3.setVisibility(View.GONE);
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
                 final String s=strings[0].replace("GDREADR","");
                 StringBuffer sb=new StringBuffer();
                 sb.append(s.substring(0, 4)+"-");
                 sb.append(s.substring(4, 6)+"-");
                 sb.append(s.substring(6, 8)+" ");
                 sb.append(s.substring(8, 10)+":00");
                 etCStime.setText(sb.toString());
                 etCEtime.setText((Integer.parseInt(strings[1])/60)+"");
                 break;
            //显示发送频率
            case BleContant.SEND_FA_SONG_PIN_LU:
                  strings=data.split(",");
                  final String hour=strings[0].replace("GDSENDR","");
                  etFStime.setText(mFormatter1.format(new Date())+" "+hour+":00");
                  if(strings[1].equals("00")){
                      etFEtime.setText("0");
                  }else{
                      etFEtime.setText(Util.delete_ling(strings[1]));
                  }
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
        myIntentFilter.addAction(BleService.ACTION_SEND_DATA_FAIL);//发送数据失败
        myIntentFilter.addAction(BleService.ACTION_GET_DATA_ERROR);//回执error数据
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
                         dialogView = new DialogView(mContext, "参数设置成功！", "确定",null, new View.OnClickListener() {
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
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS,SEND_TYPE);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    clearTask();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS,SEND_TYPE);
                        }
                    }, null);
                    dialogView.show();
                     break;
                case BleService.ACTION_GET_DATA_ERROR:
                    clearTask();
                    showToastView("设备回执数据异常！");
                default:
                    break;
            }
        }
    };


    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH");
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        public void onDateTimeSet(Date date) {
            if(TimeUtils.type==0){
                etCStime.setText(mFormatter.format(date)+":00");
            }else{
                etFStime.setText(mFormatter.format(date)+":00");
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
