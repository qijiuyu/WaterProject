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
 * 实时数据查询
 * Created by Administrator on 2018/9/2.
 */

public class GetDataActivity extends BaseActivity {

    private TextView tvCJTime,tvMaiShen,tvYaLi,tvQiYa,tvShuiWen,tvQiWen,tvDianYa,tvXinHao;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_getdata);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_1fc37f);
        initView();
        register();//注册广播
        sendData(); //发送蓝牙命令
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("实时数据");
        tvCJTime=(TextView)findViewById(R.id.tv_ag_cjTime);
        tvMaiShen=(TextView)findViewById(R.id.tv_ag_maishen);
        tvYaLi=(TextView)findViewById(R.id.tv_ag_yali);
        tvQiYa=(TextView)findViewById(R.id.tv_ag_qiya);
        tvShuiWen=(TextView)findViewById(R.id.tv_ag_shuiwen);
        tvQiWen=(TextView)findViewById(R.id.tv_ag_qiwen);
        tvDianYa=(TextView)findViewById(R.id.tv_ag_dianya);
        tvXinHao=(TextView)findViewById(R.id.tv_ag_xinhao);
        //查询实时数据
        findViewById(R.id.tv_get).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData();
            }
        });
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 发送蓝牙命令
     */
    private void sendData(){
        //判断蓝牙是否打开
        if(!BleUtils.isEnabled(GetDataActivity.this,MainActivity.mBtAdapter)){
            return;
        }
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
                            sendData();
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
                                    MainActivity.bleService.connect(GetDataActivity.this.ble.getBleMac());
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
                                            MainActivity.bleService.connect(GetDataActivity.this.ble.getBleMac());
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
                     sendData();
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                     clearTask();
                     final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                     //解析并显示回执的数据
                     showData(data);
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    clearTask();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    clearTask();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                    clearTask();
                    showToastView("设备回执数据异常");
                default:
                    break;
            }
        }
    };

    /**
     * 展示数据
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
        tvCJTime.setText(stringBuffer.toString());

        //显示压力值
        final String YaLi=msg.substring(52,61).replace("P","");
        if(YaLi.contains("99999999")){
            tvYaLi.setText(YaLi+"");
        }else{
            tvYaLi.setText(Util.setDouble(Double.parseDouble(YaLi),3)+"");
        }

        //显示水位埋深
        String MaiShen=msg.substring(12,20).replace("L","");
        if(YaLi.contains("99999999")){
            tvMaiShen.setText(MaiShen+"m");
        }else{
            tvMaiShen.setText(Util.setDouble(Double.parseDouble(MaiShen),2)+"m");
        }

        //显示气压值
        final String QiYa=msg.substring(61,68).replace("B","");
        if(YaLi.contains("99999999")){
            tvQiYa.setText(QiYa+"");
        }else{
            tvQiYa.setText(Util.setDouble(Double.parseDouble(QiYa),3)+"");
        }

        //显示水温值
        final String ShuiWen=msg.substring(20,26).replace("T","");
        if(YaLi.contains("99999999")){
            tvShuiWen.setText(ShuiWen+"℃");
        }else{
            tvShuiWen.setText(Double.parseDouble(ShuiWen)+"℃");
        }

        //显示气温值
        final String QiWen=msg.substring(41,47).replace("R","");
        if(YaLi.contains("99999999")){
            tvQiWen.setText(QiWen+"℃");
        }else{
            tvQiWen.setText(Double.parseDouble(QiWen)+"℃");
        }

        //显示电压值
        final String DianYa=msg.substring(30,36).replace("V","");
        if(YaLi.contains("99999999")){
            tvDianYa.setText(DianYa+"V");
        }else{
            tvDianYa.setText(Util.setDouble(Double.parseDouble(DianYa),1)+"V");
        }

        //现实信号值
//        final String XinHao=msg.substring(36,41).replace("CSQ","");
//        tvXinHao.setText(XinHao);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
