package com.water.project.activity.menu6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.activity.MainActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.presenter.SendDataPersenter;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * 发送数据
 * Created by Administrator on 2019/11/15.
 */

public class SendDataActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    //MVP对象
    private SendDataPersenter sendDataPersenter;
    //下发命令的编号
    private int SEND_STATUS;
    //当前页面是否在显示
    private boolean isShowActivity=true;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_data);
        ButterKnife.bind(this);
        initView();
        //注册广播
        register();
        //发送命令
        sendData(BleContant.GET_DEVICE_MODEL);
    }

    /**
     * 初始化
     */
    private void initView() {
        //注册EventBus
        EventBus.getDefault().register(this);
        //实例化MVP
        sendDataPersenter = new SendDataPersenter(this);
        tvHead.setText("发送数据");

        //返回
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendDataActivity.this.finish();
            }
        });
    }


    /**
     * 发送蓝牙命令
     */
    public void sendData(int status) {
        SEND_STATUS = status;
        //判断蓝牙是否打开
        if (!BleUtils.isEnabled(SendDataActivity.this, MainActivity.mBtAdapter)) {
            return;
        }
        //如果蓝牙连接断开，就扫描重连
        if (MainActivity.bleService.connectionState == MainActivity.bleService.STATE_DISCONNECTED) {
            //扫描并重连蓝牙
            final Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
            if (null != ble) {
                DialogUtils.showProgress(SendDataActivity.this, "扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        DialogUtils.showProgress(SendDataActivity.this, "正在获取设备型号...");
        SendBleStr.sendBleData(SEND_STATUS);
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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReceive(Context context, Intent intent) {
            if(!isShowActivity){
                return;
            }
            switch (intent.getAction()) {
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    sendDataPersenter.resumeScan(SEND_STATUS);
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    sendDataPersenter.bleDisConnect();
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    if (SEND_STATUS == BleContant.NOT_SEND_DATA) {
                        showToastView("蓝牙连接成功！");
                    } else {
                        sendData(SEND_STATUS);
                    }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    DialogUtils.closeProgress();
                    try {
                        final String data = intent.getStringExtra(BleService.ACTION_EXTRA_DATA).replace(">OK","");
                        //解析数据
                        String[] strs=data.split("-");
                        if(strs!=null && strs.length>0){
                            String model=strs[strs.length-1];
                            Intent gotoIntent=new Intent();
                            if(model.startsWith("G")){
                                gotoIntent.setClass(SendDataActivity.this,GActivity.class);
                            }
                            if(model.startsWith("B")){
                                gotoIntent.setClass(SendDataActivity.this,BActivity.class);
                            }
                            if(model.startsWith("S")){
                                return;
                            }
                            startActivity(gotoIntent);
                            finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    sendDataPersenter.timeOut(SEND_STATUS);
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    sendDataPersenter.sendCmdFail(SEND_STATUS);
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                    DialogUtils.closeProgress();
                    showToastView("设备回执数据异常！");
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * EventBus注解
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe
    public void onEvent(EventType eventType) {
        if(!isShowActivity){
            return;
        }
        switch (eventType.getStatus()) {
            //发送命令
            case EventStatus.SEND_CHECK_MCD:
                final int cmd = (int) eventType.getObject();
                sendData(cmd);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShowActivity=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isShowActivity=false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
    }
}
