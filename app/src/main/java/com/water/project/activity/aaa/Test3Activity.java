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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.activity.MainActivity;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.SendBleDataManager;
import com.water.project.view.DialogView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 1：简单的发送命令，然后界面显示设备回执的数据
 * 2：可以编辑2000个字符发送，接收的数据可以上下切换看
 * 3：超时命令改为30s
 * Created by Administrator on 2020/2/28.
 */

public class Test3Activity extends BaseActivity {

    @BindView(R.id.et_msg)
    EditText etMsg;
    @BindView(R.id.tv_send_num)
    TextView tvSendNum;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.tv_show_data)
    TextView tvShowData;
    @BindView(R.id.tv_show_num)
    TextView tvShowNum;
    //发送的命令
    private String cmd;
    public static BleService bleService = null;
    public static BluetoothAdapter mBtAdapter = null;
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        ButterKnife.bind(this);
        initView();
        initService();
        register();
    }


    /**
     * 初始化
     */
    private void initView(){
        etMsg.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                tvSendNum.setText("个数："+s.toString().length());
            }
        });

        tvShowData.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                tvShowNum.setText("个数："+s.toString().length());
            }
        });
    }

    @OnClick({ R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                cmd=etMsg.getText().toString().trim();
                if(TextUtils.isEmpty(cmd)){
                    ToastUtil.showLong("请输入要发送的命令");
                }else{
                    if(bleService==null){
                        initService();
                        return;
                    }
                    tvShowData.setText(null);
                    if(bleService.connectionState==bleService.STATE_DISCONNECTED){
                        DialogUtils.showProgress(Test3Activity.this,"扫描并连接蓝牙设备...");
                        bleService.scanDevice("ZKGDBluetooth");
                    }else{
                        DialogUtils.showProgress(Test3Activity.this,"发送命令中...");
                        if(cmd.startsWith("GDRECORDA")){
                            SendBleDataManager.getInstance().sendData(cmd,true,2);
                        }else{
                            SendBleDataManager.getInstance().sendData(cmd,true,1);
                        }
                    }
                }
                break;
            default:
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
            //设置超时时间
            bleService.setTimeOut(30*1000);
            //判断蓝牙是否打开
            BleUtils.isEnabled(Test3Activity.this,mBtAdapter);
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
                    dialogView = new DialogView(Test3Activity.this, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(Test3Activity.this,"扫描并连接蓝牙设备...");
                            bleService.scanDevice("ZKGDBluetooth");
                        }
                    }, null);
                    dialogView.show();
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(Test3Activity.this, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(Test3Activity.this,"扫描并连接蓝牙设备...");
                            bleService.scanDevice("ZKGDBluetooth");
                        }
                    }, null);
                    dialogView.show();
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    DialogUtils.showProgress(Test3Activity.this,"发送命令中...");
                    if(cmd.startsWith("GDRECORDA")){
                        SendBleDataManager.getInstance().sendData(cmd,true,2);
                    }else{
                        SendBleDataManager.getInstance().sendData(cmd,true,1);
                    }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    DialogUtils.closeProgress();
                    String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    tvShowData.setText(data);
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(Test3Activity.this, "接收数据超时！", "确定",null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(Test3Activity.this, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(Test3Activity.this,"发送命令中...");
                            if(cmd.startsWith("GDRECORDA")){
                                SendBleDataManager.getInstance().sendData(cmd,true,2);
                            }else{
                                SendBleDataManager.getInstance().sendData(cmd,true,1);
                            }
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
