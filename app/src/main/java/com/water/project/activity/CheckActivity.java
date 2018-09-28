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

/**
 * 数据校验
 * Created by Administrator on 2018/9/2.
 */

public class CheckActivity extends BaseActivity {
    private TextView tvTime,tvYaLi,tvQiYa,tvTanTou,tvShuiWei,tvWuCha,tvDes;
    private EditText etCheck;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    //手动输入的误差数据
    private String wuCha;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_check);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_1fc37f);
        initView();
        register();//注册广播
//        sendData(BleContant.SEND_REAL_TIME_DATA);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("数据校测");
        tvTime=(TextView)findViewById(R.id.tv_ac_cjTime);
        tvYaLi=(TextView)findViewById(R.id.tv_ac_yali);
        tvQiYa=(TextView)findViewById(R.id.tv_ac_qiya);
        tvTanTou=(TextView)findViewById(R.id.tv_ac_tantou);
        tvShuiWei=(TextView)findViewById(R.id.tv_ac_shuiwei);
        etCheck=(EditText)findViewById(R.id.et_ac_check);
        tvWuCha=(TextView)findViewById(R.id.tv_ac_wucha);
        tvDes=(TextView)findViewById(R.id.tv_check_des);
        //数据校验
        findViewById(R.id.tv_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String YaLi=tvYaLi.getText().toString().trim();
                wuCha=tvWuCha.getText().toString().trim();
                if(YaLi.contains("99999999")){
                    dialogView = new DialogView(mContext, "传感器故障，无法参与计算校准，请查找原因！", "知道了",null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                        }
                    }, null);
                    dialogView.show();
                    return;
                }
                if(TextUtils.isEmpty(wuCha)){
                    showToastView("没有误差数据！");
                }else{
                    etCheck.setText("");
                    tvWuCha.setText("");
//                    sendData(BleContant.SEND_CHECK_ERROR);
                    SendBleStr.setCheck(wuCha,"");
                }
            }
        });
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        etCheck.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                final String YaLi=tvYaLi.getText().toString().trim();
                if(YaLi.contains("99999999")){
                    dialogView = new DialogView(mContext, "传感器故障，无法参与计算校准，请查找原因！", "知道了",null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                        }
                    }, null);
                    dialogView.show();
                    return;
                }

                final String strShui=tvShuiWei.getText().toString().trim().replace("m","");
                final double shuiWei=Double.parseDouble(strShui);

                if(TextUtils.isEmpty(s.toString())){
                    tvWuCha.setText("");
                    tvDes.setText("");
                    return;
                }
                final double check=Double.parseDouble(s.toString());
                //显示误差
                final String wuCha=Util.sub(shuiWei,check)+"";
                tvWuCha.setText(wuCha);

                double d=Double.parseDouble(wuCha.replace("-",""))*100;
                if(d<2){
                    tvDes.setText("误差小于2CM，无需进行调整");
                }else if(d>10){
                    tvDes.setText("不建议点击修正误差，请先检查原因");
                }else{
                    tvDes.setText("");
                }
            }
        });
    }


    /**
     * 发送蓝牙命令
     */
    private void sendData(int status){
        //判断蓝牙是否打开
        if(!BleUtils.isEnabled(CheckActivity.this,MainActivity.mBtAdapter)){
            return;
        }
        SEND_STATUS=status;
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
        SendBleStr.sendBleData(status,1);
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
                                    MainActivity.bleService.connect(CheckActivity.this.ble.getBleMac());
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
                                            MainActivity.bleService.connect(CheckActivity.this.ble.getBleMac());
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
                        sendData(SEND_STATUS);
                    }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                     final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                     if(SEND_STATUS==BleContant.SEND_REAL_TIME_DATA){
                         //解析并显示回执的数据
                         showData(data);
                         clearTask();
                         SEND_STATUS=BleContant.NOT_SEND_DATA;
                     }else if(SEND_STATUS==BleContant.SEND_CHECK_ERROR){
                         SendBleStr.setCheck(wuCha,data);
                         sendData(BleContant.SET_DATA_CHECK);
                     }else{
                         sendData(BleContant.SEND_REAL_TIME_DATA);
                     }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    clearTask();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    clearTask();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                     clearTask();
                     showToastView("设备回执数据异常！");
                     break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示数据
     */
    private void showData(String msg){
        //显示采集时间
        msg=msg.replace("GDCURRENT>","");
        StringBuffer stringBuffer=new StringBuffer("20");
        stringBuffer.append(msg.substring(0,2)+"-");
        stringBuffer.append(msg.substring(2,4)+"-");
        stringBuffer.append(msg.substring(4,6)+" ");
        stringBuffer.append(msg.substring(6,8)+":");
        stringBuffer.append(msg.substring(8,10)+":");
        stringBuffer.append(msg.substring(10,12));
        tvTime.setText(stringBuffer.toString());

        //显示压力值
        final String YaLi=msg.substring(52,61).replace("P","");
        if(YaLi.contains("99999999")){
            tvYaLi.setText(YaLi+"");
        }else{
            tvYaLi.setText(Util.setDouble(Double.parseDouble(YaLi),3)+"");
        }

        //显示气压值
        final String QiYa=msg.substring(61,68).replace("B","");
        if(YaLi.contains("99999999")){
            tvQiYa.setText(QiYa+"");
        }else{
            tvQiYa.setText(Util.setDouble(Double.parseDouble(QiYa),3)+"");
        }

        //显示探头埋深
        final String TanTou=msg.substring(68,77).replace("C","");
        if(YaLi.contains("99999999")){
            tvTanTou.setText(TanTou+"m");
        }else{
            tvTanTou.setText(Util.setDouble(Double.parseDouble(TanTou),3)+"m");
        }

        //显示水位埋深
        final String MaiShen=msg.substring(12,20).replace("L","");
        if(YaLi.contains("99999999")){
            tvShuiWei.setText(MaiShen+"m");
        }else{
            tvShuiWei.setText(Util.setDouble(Double.parseDouble(MaiShen),2)+"m");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
