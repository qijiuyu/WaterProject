package com.water.project.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.water.project.R;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.view.DialogView;

public class MainActivity extends BaseActivity {

    //蓝牙参数
    public static BleService bleService = null;
    public BluetoothAdapter mBtAdapter = null;
    private DialogView dialogView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_1fc37f);
        initView();
        //判断设备是否支持蓝牙4.0
        isSupportBle();
        initService();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        findViewById(R.id.tv_am).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               setClass(SearchBleActivity.class);
            }
        });
    }


    /**
     * 打开蓝牙service
     */
    private void initService() {
        Intent bindIntent = new Intent(this, BleService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            bleService = ((BleService.LocalBinder) rawBinder).getService();
            mBtAdapter = bleService.createBluetoothAdapter();
            //判断蓝牙是否打开
            BleUtils.isEnabled(MainActivity.this,mBtAdapter);
        }

        public void onServiceDisconnected(ComponentName classname) {
        }
    };


    /**
     * 判断设备是否支持蓝牙4.0
     */
    private void isSupportBle(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            dialogView = new DialogView(mContext, "该设备不支持ble蓝牙!","知道了", null, new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                    finish();
                }
            }, null);
            dialogView.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭服务
        unbindService(mServiceConnection);
    }
}
