package com.water.project.activity.aaa;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.activity.MainActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.FileUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleDataManager;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

import java.util.Calendar;

/**
 * Created by Administrator on 2019/9/29.
 */

public class TestActivity extends BaseActivity {

    //蓝牙参数
    public static BleService bleService = null;
    public static BluetoothAdapter mBtAdapter = null;
    private DialogView dialogView;
    private String blutoothName;
    private Handler mHandler=new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initService();
        register();


        final EditText etName=(EditText)findViewById(R.id.et_name);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blutoothName=etName.getText().toString().trim();
                if(TextUtils.isEmpty(blutoothName)){
                    ToastUtil.showLong("请输入设备的蓝牙名称");
                }else{
                    DialogUtils.showProgress(TestActivity.this,"扫描并连接蓝牙设备...");
                    bleService.scanDevice(blutoothName);
                }
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
                            DialogUtils.showProgress(TestActivity.this,"扫描并连接蓝牙设备...");
                            bleService.scanDevice(blutoothName);
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
                            DialogUtils.showProgress(TestActivity.this,"蓝牙连接中...");
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
                    DialogUtils.showProgress(TestActivity.this,"发送命令中...");
                    SendBleDataManager.getInstance().sendData("GDERRREAD",true,1);
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    Calendar calendar = Calendar.getInstance();
                    //年
                    int intYear = calendar.get(Calendar.YEAR);
                    //月
                    int intMonth = (calendar.get(Calendar.MONTH)+1);
                    //日
                    int intDay = calendar.get(Calendar.DAY_OF_MONTH);
                    //小时
                    int intHour = calendar.get(Calendar.HOUR_OF_DAY);
                    //分钟
                    int intMinute=calendar.get(Calendar.MINUTE);
                    //秒钟
                    int secound=calendar.get(Calendar.SECOND);
                    final String fileName=data.substring(9,24)+"_"+intYear+intMonth+intDay+intHour+intMinute+secound+".txt";
                    String filePath=FileUtils.createFile(fileName,data);
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "数据.txt文件已创建成功，目录是："+filePath, "确定",null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "接收数据超时！", "确定",null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(TestActivity.this,"发送命令中...");
                            SendBleDataManager.getInstance().sendData("GDERRREAD",true,1);
                        }
                    }, null);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=bleService){
            bleService.disconnect();
        }
        unbindService(mServiceConnection);
    }
}
