package com.water.project.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.bean.BindService;
import com.water.project.bean.Ble;
import com.water.project.presenter.AboutPersenter;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.FileUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.PathUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.BleObject;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于我们
 * Created by Administrator on 2018/7/4 0004.
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    private AboutPersenter aboutPersenter;
    private DialogView dialogView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initView();
        register();//注册广播
    }

    private void initView() {
        tvHead.setText("关于我们");
        aboutPersenter=new AboutPersenter(this);
    }

    @OnClick({R.id.lin_back, R.id.tv_btn, R.id.tv_write})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                finish();
                break;
            case R.id.tv_btn:
                setClass(VersionActivity.class);
                break;
            //更新字库信息
            case R.id.tv_write:
                aboutPersenter.showDialog();
                break;
            default:
                break;
        }
    }


    /**
     * 发送蓝牙命令
     */
    private BleService bleService;
    public void sendData() {
        bleService= BleObject.getInstance().getBleService(this, new BindService() {
            @Override
            public void onSuccess() {
                sendData();
            }
        });
        if(bleService==null){
            return;
        }
        DialogUtils.showProgress(AboutActivity.this, "正在发送数据...");
        //如果蓝牙连接断开，就扫描重连
        if (bleService.connectionState == bleService.STATE_DISCONNECTED) {
            //扫描并重连蓝牙
            final Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
            if (null != ble) {
                DialogUtils.showProgress(AboutActivity.this, "扫描并连接蓝牙设备...");
                bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(this,BleContant.SEND_TXT_CONTENT);
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
            switch (intent.getAction()) {
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(dialogView,mContext, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData();
                        }
                    }, null);
                    dialogView.show();
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(dialogView,mContext, "蓝牙连接断开，请靠近设备进行连接!", "重新连接", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(AboutActivity.this, "GPRS连接中...");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
                                    bleService.connect(ble.getBleMac());
                                }
                            }, 100);
                        }
                    }, null);
                    dialogView.show();
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    sendData();
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    if(!aboutPersenter.sendBigData()){
                        DialogUtils.closeProgress();
                        dialogView = new DialogView(dialogView,mContext, "更新字库信息成功！", "好的", null, null, null);
                        dialogView.show();
                    }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(dialogView,mContext, "更新字库信息失败，请重试！", "知道了", null, null, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(dialogView,mContext, "更新字库信息失败，请重试！", "知道了", null, null, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                    DialogUtils.closeProgress();
                    showToastView("设备回执数据异常");
                    break;
                default:
                    break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        File file = null;
        if (requestCode == 100 && resultCode == Activity.RESULT_OK ) {
            try {
                String path= PathUtils.getPath(mContext,data.getData());
                if(TextUtils.isEmpty(path)){
                    ToastUtil.showLong("找不到该文档的路径");
                    return;
                }
                file=new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(resultCode==200){
            String path=data.getStringExtra("path");
            file=new File(path);
        }

        if(!file.isFile()){
            ToastUtil.showLong("文件路径有误");
            return;
        }
        String str= FileUtils.readTxt(file.getPath()).replace("0x","").replace(",","");
        if(str.length()==9602 || str.length()==66 || str.length()==34){
            aboutPersenter.parsingData(str);
        }else{
            ToastUtil.showLong("文件内容错误,不予执行");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
