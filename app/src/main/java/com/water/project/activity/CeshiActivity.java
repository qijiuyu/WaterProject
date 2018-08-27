package com.water.project.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.water.project.R;
import com.water.project.service.BleService;
import com.water.project.utils.LogUtils;

public class CeshiActivity extends BaseActivity {

    //蓝牙参数
    public BleService mService = null;
    public BluetoothAdapter mBtAdapter = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aaa);

        initService();
        registerBoradcastReceiver();

        final EditText editText=(EditText)findViewById(R.id.et_name);

        findViewById(R.id.btn_con).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mService.connectScan(editText.getText().toString().trim());
            }
        });


        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str="01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
                final int length=str.length();
                String[] msg=new String[length/20];

                int index1=0,index2=20;
                int i=0;
                while(index2<=length){
                    msg[i]=str.substring(index1, index2);
                    i++;
                    index1+=20;
                    index2+=20;
                }
                for (int j=0;j<msg.length;j++){
                     LogUtils.e(msg[j]);
                     LogUtils.e(System.currentTimeMillis()+"");
                }

                mService.writeRXCharacteristic(msg,true);
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
            mService = ((BleService.LocalBinder) rawBinder).getService();
            mBtAdapter = mService.createBluetoothAdapter();
        }

        public void onServiceDisconnected(ComponentName classname) {
        }
    };


    /**
     * 注册广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_GATT_CONNECTED);//蓝牙链接成功
        myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);//蓝牙断开连接
        myIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);//接到的数据
        myIntentFilter.addAction(BleService.ACTION_NO_DISCOVERY_BLE);//没有发现想要连接的蓝牙
        myIntentFilter.addAction(BleService.ACTION_INTERACTION_TIMEOUT);//发送命令超时
        myIntentFilter.addAction(BleService.ACTION_ENABLE_NOTIFICATION_SUCCES);//初始化通信通道成功
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action=intent.getAction();
            switch (action){
                case BleService.ACTION_NO_DISCOVERY_BLE://没有发现要连接的蓝牙:
                     showToastView("扫描不到该蓝牙设备");
                     break;
                case BleService.ACTION_GATT_DISCONNECTED://蓝牙连接断开
                     break;
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES://初始化通信通道成功，发送imei号验证
                     LogUtils.e("蓝牙初始化通信通道成功");
                    break;
                    default:
                        break;
            }

        }
    };
}