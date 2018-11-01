package com.water.project.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.SendBleDataManager;
import com.water.project.view.DialogView;

/**
 * 测试工具类
 */
public class TestActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvShow;
    private EditText etMsg;
    //蓝牙参数
    public static BleService bleService = null;
    public static BluetoothAdapter mBtAdapter = null;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aaa);
        initView();
        initService();//注册蓝牙服务
        register();//注册广播
    }


    /**
     * 初始化
     */
    private void initView(){
        etMsg=(EditText)findViewById(R.id.et_msg);
        final TextView tvSendNum=(TextView)findViewById(R.id.tv_send_num);
        tvShow=(TextView)findViewById(R.id.tv_show_data);
        final TextView tvShowNum=(TextView)findViewById(R.id.tv_show_num);

        etMsg.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                tvSendNum.setText("数据长度是："+s.toString().length());
            }
        });

        tvShow.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                tvShowNum.setText("数据长度是："+s.toString().length());
            }
        });

        findViewById(R.id.btn_con).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //扫描并连接
            case R.id.btn_con:
                if(bleService.connectionState==bleService.STATE_DISCONNECTED){
                    Ble ble=new Ble();
                    ble.setBleName("ZKGDBluetooth");
                    MyApplication.spUtil.addObject(SPUtil.BLE_DEVICE,ble);
                    showProgress("扫描并连接中...");
                    bleService.scanDevice("ZKGDBluetooth");
                }
                 break;
            //发送数据
            case R.id.btn_send:
                if(bleService.connectionState==bleService.STATE_DISCONNECTED){
                    dialogView = new DialogView(mContext, "蓝牙连接断开，请重新连接!","确认", null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            showProgress("扫描并连接中...");
                            bleService.scanDevice("ZKGDBluetooth");
                        }
                    }, null);
                    dialogView.show();
                    return;
                }
                 final String msg=etMsg.getText().toString().trim();
                 if(TextUtils.isEmpty(msg)){
                     showToastView("请输入要发送的命令！");
                 }else{
                     tvShow.setText(null);
                     showProgress("发送命令中...");
                     SendBleDataManager.getInstance().sendData(msg,1);
                 }
                 break;
        }
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
            BleUtils.isEnabled(TestActivity.this,mBtAdapter);
        }

        public void onServiceDisconnected(ComponentName classname) {
        }
    };



    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_NO_DISCOVERY_BLE);//扫描不到该蓝牙设备
        myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);//蓝牙断开连接
        myIntentFilter.addAction(BleService.ACTION_ENABLE_NOTIFICATION_SUCCES);//蓝牙初始化通道成功
        myIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);//接收回执的数据
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private DialogView dialogView;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            clearTask();
            switch (intent.getAction()){
                //扫描不到该蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                     dialogView = new DialogView(mContext, "未搜索到到蓝牙设备!","确认", null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                         }
                    }, null);
                     dialogView.show();
                     break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    dialogView = new DialogView(mContext, "蓝牙连接断开，请重新连接!","确认", null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            showProgress("扫描并连接中...");
                            bleService.scanDevice("ZKGDBluetooth");
                        }
                    }, null);
                    dialogView.show();
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    dialogView = new DialogView(mContext, "蓝牙连接成功!","确认", null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_DATA_AVAILABLE:
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    tvShow.append(data);
                     break;
                default:
                    break;
            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        unregisterReceiver(mBroadcastReceiver);
    }
}
