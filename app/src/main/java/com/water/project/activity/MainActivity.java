package com.water.project.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.water.project.R;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.utils.ble.SendBleDataManager;
import com.water.project.view.DialogView;
import com.water.project.view.time.SlideDateTimeListener;
import com.water.project.view.time.SlideDateTimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseActivity {

    // 按两次退出
    protected long exitTime = 0;
    //蓝牙参数
    public static BleService bleService = null;
    public BluetoothAdapter mBtAdapter = null;
    private DialogView dialogView;
    //蓝牙连接成功的广播
    public final static String ACTION_BLE_CONNECTION_SUCCESS= "net.zkgd.adminapp.ACTION_BLE_CONNECTION_SUCCESS";
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");
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
        isSupportBle();//判断设备是否支持蓝牙4.0
        initService();//注册蓝牙服务
        register();//注册广播

        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                //.setMinDate(minDate)
                //.setMaxDate(maxDate)
                //.setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();
    }


    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            Toast.makeText(MainActivity.this,
                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(MainActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };


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
            SendBleDataManager.getInstance().init(bleService);
            //判断蓝牙是否打开
            BleUtils.isEnabled(MainActivity.this,mBtAdapter);
        }

        public void onServiceDisconnected(ComponentName classname) {
        }
    };


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_BLE_CONNECTION_SUCCESS);//扫描到蓝牙设备了
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(null==intent){
                return;
            }
            switch (intent.getAction()){
                //蓝牙连接成功
                case ACTION_BLE_CONNECTION_SUCCESS:
                    break;
                default:
                    break;
            }

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


    // 按两次退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToastView("再按一次退出程序！");
                exitTime = System.currentTimeMillis();
            } else {
                unbindService(mServiceConnection);
                unregisterReceiver(mBroadcastReceiver);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
