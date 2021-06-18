package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.bean.BindService;
import com.water.project.bean.Ble;
import com.water.project.bean.SelectTime;
import com.water.project.service.BleService;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.Util;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.BleObject;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import com.water.project.view.SelectTimeDialog;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 参数设置
 * Created by Administrator on 2018/9/1.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener,SelectTime{
    private EditText etCode,etPhone,etTanTou;
    private TextView etCStime,etFStime,etCEtime,etFEtime,tvNewTime;
    private ImageView imgClear1,imgClear2,imgClear3;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    /**
     * 1：刚进入读取命令
     * 2： 单独读取，设置命令
     */
    private int SEND_TYPE;
    //设置统一编码和SIM的数据
    private String CODE_SIM_DATA;
    private SimpleDateFormat mFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
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
        tvNewTime=findViewById(R.id.tv_new_time);
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
        tvNewTime.setOnClickListener(this);
        findViewById(R.id.tv_setting_one).setOnClickListener(this);
        findViewById(R.id.tv_setting_two).setOnClickListener(this);
        findViewById(R.id.tv_setting_three).setOnClickListener(this);
        findViewById(R.id.tv_setting_four).setOnClickListener(this);
        findViewById(R.id.tv_get_one).setOnClickListener(this);
        findViewById(R.id.tv_get_two).setOnClickListener(this);
        findViewById(R.id.tv_get_three).setOnClickListener(this);
        findViewById(R.id.tv_get_four).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.tv_setting_five).setOnClickListener(this);
        findViewById(R.id.tv_get_five).setOnClickListener(this);
        etCode.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
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
     * @param SEND_STATUS
     */
    private BleService bleService;
    private void sendData(final int SEND_STATUS, final int type){
        this.SEND_STATUS=SEND_STATUS;
        SEND_TYPE=type;
        bleService= BleObject.getInstance().getBleService(this, new BindService() {
            @Override
            public void onSuccess() {
                sendData(SEND_STATUS,type);
            }
        });
        if(bleService==null){
            return;
        }
        switch (SEND_STATUS){
            case BleContant.SEND_GET_CODE_PHONE:
            case BleContant.SEND_GET_TANTOU:
            case BleContant.SEND_CAI_JI_PIN_LU:
            case BleContant.SEND_FA_SONG_PIN_LU:
            case BleContant.RED_DEVICE_TIME:
                DialogUtils.showProgress(SettingActivity.this,"正在读取参数设置...");
                 break;
             default:
                 DialogUtils.showProgress(SettingActivity.this,"正在设置参数信息...");
                 break;
        }
        //如果蓝牙连接断开，就扫描重连
        if(bleService.connectionState==bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                DialogUtils.showProgress(SettingActivity.this,"扫描并连接蓝牙设备...");
                bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(this,SEND_STATUS);
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
                 SelectTimeDialog selectTimeDialog=new SelectTimeDialog(SettingActivity.this,this,1);
                 selectTimeDialog.show();
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
                 SelectTimeDialog selectTimeDialog2=new SelectTimeDialog(SettingActivity.this,this,2);
                 selectTimeDialog2.show();
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
            //选择时间---设置设备时间用
            case R.id.tv_new_time:
                SelectTimeDialog selectTimeDialog3=new SelectTimeDialog(SettingActivity.this,this,5);
                selectTimeDialog3.show();
                break;
            //设置设备时间
            case R.id.tv_setting_five:
                SendBleStr.sendDeviceTime(tvNewTime.getText().toString().trim());
                sendData(BleContant.SEND_DEVICE_TIME,2);
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
            //读取设备时间
            case R.id.tv_get_five:
                  sendData(BleContant.RED_DEVICE_TIME,2);
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
        BuglyUtils.uploadBleMsg("参数设置界面读取的数据是："+data);

        try {
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
                //显示设备时间
                case BleContant.RED_DEVICE_TIME:
                      final String time=data.replace("GDTIMER","");
                      StringBuffer sb2=new StringBuffer("20");
                      sb2.append(time.substring(0, 2)+"-");
                      sb2.append(time.substring(2,4)+"-");
                      sb2.append(time.substring(4,6)+" ");
                      sb2.append(time.substring(6,8)+":");
                      sb2.append(time.substring(8,10)+":");
                      sb2.append(time.substring(10,12));
                      tvNewTime.setText(sb2.toString());
                      break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
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
        myIntentFilter.addAction(BleService.ACTION_INTERACTION_TIMEOUT);//发送命令超时
        myIntentFilter.addAction(BleService.ACTION_SEND_DATA_FAIL);//发送数据失败
        myIntentFilter.addAction(BleService.ACTION_GET_DATA_ERROR);//回执error数据
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    DialogUtils.closeProgress();
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
                    DialogUtils.closeProgress();
                     dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(SettingActivity.this,"GPRS连接中...");
                            mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                                    bleService.connect(ble.getBleMac());
                                }
                            },100);
                         }
                    }, null);
                     dialogView.show();
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
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA).replace(">OK","");

                    //刚进入界面读取的操作
                    if(SEND_TYPE==1){
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
                                sendData(BleContant.RED_DEVICE_TIME,1);
                                break;
                            case BleContant.RED_DEVICE_TIME:
                                 DialogUtils.closeProgress();
                                 SEND_STATUS=BleContant.NOT_SEND_DATA;
                                 break;
                            default:
                               DialogUtils.closeProgress();
                                break;
                        }
                    }

                    //单独读取与设置等操作
                    if(SEND_TYPE==2){
                       DialogUtils.closeProgress();
                        if(SEND_STATUS==BleContant.SEND_GET_CODE_PHONE || SEND_STATUS==BleContant.SEND_GET_TANTOU || SEND_STATUS==BleContant.SEND_CAI_JI_PIN_LU || SEND_STATUS==BleContant.SEND_FA_SONG_PIN_LU || SEND_STATUS==BleContant.RED_DEVICE_TIME){
                            //解析并显示回执的数据
                            showData(data);
                        }else{
                            dialogView = new DialogView(mContext, "参数设置成功！", "确定",null, new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialogView.dismiss();
                                }
                            }, null);
                            dialogView.show();
                        }
                        SEND_STATUS=BleContant.NOT_SEND_DATA;
                    }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                   DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS,SEND_TYPE);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                   DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS,SEND_TYPE);
                        }
                    }, null);
                    dialogView.show();
                     break;
                case BleService.ACTION_GET_DATA_ERROR:
                   DialogUtils.closeProgress();
                    showToastView("设备回执数据异常！");
                default:
                    break;
            }
        }
    };

    public void getTime(String time,int type) {
        if(type==1){
            etCStime.setText(time+":00");
        }
        if(type==2){
            etFStime.setText(time+":00");
        }
        if(type==5){
            tvNewTime.setText(time);
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

}
