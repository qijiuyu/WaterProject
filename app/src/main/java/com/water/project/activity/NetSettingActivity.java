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

import java.util.List;

public class NetSettingActivity extends BaseActivity implements View.OnClickListener{

    private ImageView img1,img2,img3;
    private EditText etAddress1,etAddress2,etAddress3,etApn,etIp1,etIp2,etIp3,etPort1,etPort2,etPort3;
    private TextView tvAddress1,tvAddress2,tvAddress3,tvIp1,tvIp2,tvIp3,tvPort1,tvPort2,tvPort3;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    //读取的数据
    private String strData;
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
        register();
//        sendData(BleContant.SEND_GET_CODE_PHONE);
    }


    /**
     * 初始化
     */
    private void initView(){
        TextView textView=(TextView)findViewById(R.id.tv_head);
        textView.setText("网络连接设置");
        img1=(ImageView)findViewById(R.id.img_an1);
        img2=(ImageView)findViewById(R.id.img_an2);
        img3=(ImageView)findViewById(R.id.img_an3);
        etAddress1=(EditText)findViewById(R.id.et_address1);
        etAddress2=(EditText)findViewById(R.id.et_address2);
        etAddress3=(EditText)findViewById(R.id.et_address3);
        etApn=(EditText)findViewById(R.id.et_apn);
        etIp1=(EditText)findViewById(R.id.et_ip1);
        etIp2=(EditText)findViewById(R.id.et_ip2);
        etIp3=(EditText)findViewById(R.id.et_ip3);
        etPort1=(EditText)findViewById(R.id.et_port1);
        etPort2=(EditText)findViewById(R.id.et_port2);
        etPort3=(EditText)findViewById(R.id.et_port3);
        tvAddress1=(TextView)findViewById(R.id.tv_address1);
        tvAddress2=(TextView)findViewById(R.id.tv_address2);
        tvAddress3=(TextView)findViewById(R.id.tv_address3);
        tvIp1=(TextView)findViewById(R.id.tv_ip1);
        tvIp2=(TextView)findViewById(R.id.tv_ip2);
        tvIp3=(TextView)findViewById(R.id.tv_ip3);
        tvPort1=(TextView)findViewById(R.id.tv_port1);
        tvPort2=(TextView)findViewById(R.id.tv_port2);
        tvPort3=(TextView)findViewById(R.id.tv_port3);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);
        findViewById(R.id.tv_red).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }


    /**
     * 发送蓝牙命令
     */
    private void sendData(int status){
        //判断蓝牙是否打开
        if(!BleUtils.isEnabled(NetSettingActivity.this,MainActivity.mBtAdapter)){
            return;
        }
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
                    if(SEND_STATUS==BleContant.NOT_SEND_DATA){
                        showToastView("蓝牙连接成功！");
                    }else{
                        sendData(SEND_STATUS);
                    }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    if(SEND_STATUS==BleContant.SEND_GET_CODE_PHONE){
                        //解析并显示回执的数据
                        showData(data);
                    }else{
                        showToastView("设置成功！");
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


    /**
     * 解析并显示数据
     */
    private  void showData(String msg){
        strData=msg;
        String[] strings=msg.split(";");
        if(null==strings || strings.length==0){
            return;
        }
        final String strAddress=strings[0];
        etAddress1.setText(strAddress.substring(3,5));
        etAddress2.setText(strAddress.substring(5,7));
        etAddress3.setText(strAddress.substring(7,9));

        //展示ip及端口号
        String strIp;
        String[] ips;
        strIp=strings[2];
        ips=strIp.split(",");
        etIp1.setText(ips[0]);
        etPort1.setText(ips[1]);

        strIp=strings[3];
        ips=strIp.split(",");
        etIp1.setText(ips[0]);
        etPort1.setText(ips[1]);

        strIp=strings[4];
        ips=strIp.split(",");
        etIp1.setText(ips[0]);
        etPort1.setText(ips[1]);

        //显示APN
        etApn.setText(strings[5]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //保存
            case R.id.tv_save:
                 final String address1=etAddress1.getText().toString().trim();
                 final String address2=etAddress2.getText().toString().trim();
                 final String address3=etAddress3.getText().toString().trim();
                 final String ip1=etIp1.getText().toString().trim();
                 final String ip2=etIp2.getText().toString().trim();
                 final String ip3=etIp3.getText().toString().trim();
                 final String port1=etPort1.getText().toString().trim();
                 final String port2=etPort2.getText().toString().trim();
                 final String port3=etPort3.getText().toString().trim();
                 final String apn=etApn.getText().toString().trim();
                 if(TextUtils.isEmpty(address1)){
                     showToastView("青输入连接1的主站地址！");
                     return;
                 }
                if(TextUtils.isEmpty(address2)){
                    showToastView("青输入连接2的主站地址！");
                    return;
                }
                if(TextUtils.isEmpty(address3)){
                    showToastView("青输入连接3的主站地址！");
                    return;
                }
                if(TextUtils.isEmpty(ip1)){
                    showToastView("青输入第一个IP地址！");
                    return;
                }
                if(TextUtils.isEmpty(ip2)){
                    showToastView("青输入第二个IP地址！");
                    return;
                }
                if(TextUtils.isEmpty(ip3)){
                    showToastView("青输入第三个IP地址！");
                    return;
                }
                if(TextUtils.isEmpty(port1)){
                    showToastView("青输入第一个端口号！");
                    return;
                }
                if(TextUtils.isEmpty(port2)){
                    showToastView("青输入第二个端口号！");
                    return;
                }
                if(TextUtils.isEmpty(port3)){
                    showToastView("青输入第三个端口号！");
                    return;
                }
                if(TextUtils.isEmpty(apn)){
                    showToastView("青输入APN！");
                    return;
                }
                 SendBleStr.setIpPort(strData,address1,address2,address3,ip1,ip2,ip3,port1,port2,port3,apn);
                 sendData(BleContant.SET_IP_PORT);
                 break;
            //读取
            case R.id.tv_red:
                 sendData(BleContant.SEND_GET_CODE_PHONE);
                 break;
            case R.id.img_an1:
                 if(null==v.getTag()){
                     return;
                 }
                 if(v.getTag().toString().equals("1")){
                     v.setTag(0);
                     img1.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
                     setColor(tvAddress1,0);
                     setColor(tvIp1,0);
                     setColor(tvPort1,0);
                     setColor2(etAddress1,0);
                     setColor2(etIp1,0);
                     setColor2(etPort1,0);
                 }else{
                     v.setTag(1);
                     img1.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
                     setColor(tvAddress1,1);
                     setColor(tvIp1,1);
                     setColor(tvPort1,1);
                     setColor2(etAddress1,1);
                     setColor2(etIp1,1);
                     setColor2(etPort1,1);
                 }
                 break;
            case R.id.img_an2:
                if(null==v.getTag()){
                    return;
                }
                if(v.getTag().toString().equals("1")){
                    v.setTag(0);
                    img2.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
                    setColor(tvAddress2,0);
                    setColor(tvIp2,0);
                    setColor(tvPort2,0);
                    setColor2(etAddress2,0);
                    setColor2(etIp2,0);
                    setColor2(etPort2,0);
                }else{
                    v.setTag(1);
                    img2.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
                    setColor(tvAddress2,1);
                    setColor(tvIp2,1);
                    setColor(tvPort2,1);
                    setColor2(etAddress2,1);
                    setColor2(etIp2,1);
                    setColor2(etPort2,1);
                }
                break;
            case R.id.img_an3:
                if(null==v.getTag()){
                    return;
                }
                if(v.getTag().toString().equals("1")){
                    v.setTag(0);
                    img3.setImageDrawable(getResources().getDrawable(R.mipmap.close_icon));
                    setColor(tvAddress3,0);
                    setColor(tvIp3,0);
                    setColor(tvPort3,0);
                    setColor2(etAddress3,0);
                    setColor2(etIp3,0);
                    setColor2(etPort3,0);
                }else{
                    v.setTag(1);
                    img3.setImageDrawable(getResources().getDrawable(R.mipmap.open_icon));
                    setColor(tvAddress3,1);
                    setColor(tvIp3,1);
                    setColor(tvPort3,1);
                    setColor2(etAddress3,1);
                    setColor2(etIp3,1);
                    setColor2(etPort3,1);
                }
                break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }
    }


    private void setColor(TextView textView,int type){
        if(type==0){
            textView.setTextColor(getResources().getColor(R.color.color_AEAEAE));
        }else{
            textView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }


    private void setColor2(EditText editText,int type){
        if(type==0){
            editText.setTextColor(getResources().getColor(R.color.color_AEAEAE));
            editText.setCursorVisible(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
        }else{
            editText.setTextColor(getResources().getColor(R.color.color_1fc37f));
            editText.setFocusable(true);
            editText.setCursorVisible(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
        }
    }
}
