package com.water.project.activity.aaa;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.SendBleDataManager;
import com.water.project.view.DialogView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/9/29.
 */

public class Test2Activity extends BaseActivity {

    //蓝牙参数
    public static BleService bleService = null;
    public static BluetoothAdapter mBtAdapter = null;
    private DialogView dialogView;
    private String blutoothName;
    private Handler mHandler=new Handler();
    private TextView tvStatus;
    /**
     * true：正在接受数据，不能离开界面
     * false：反之
     */
    private boolean isSend=false;
    private boolean isNewDevice=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //注册EventBus
        EventBus.getDefault().register(this);
        initService();
        register();

        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("发送数据");
        final EditText etName=(EditText)findViewById(R.id.et_name);
        final EditText etVersion=findViewById(R.id.et_version);
        tvStatus=findViewById(R.id.tv_status);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                blutoothName=etName.getText().toString().trim();
                String version=etVersion.getText().toString().trim();
                if(TextUtils.isEmpty(blutoothName)){
                    ToastUtil.showLong("请输入设备的蓝牙名称");
                }
                if(TextUtils.isEmpty(version)){
                    ToastUtil.showLong("请输入版本");
                }
                else{
                   tvStatus.setText(null);
                    if(version.equals("0")){
                        isNewDevice=false;
                    }else{
                        isNewDevice=true;
                    }
                    DialogUtils.showProgress(Test2Activity.this,"扫描并连接蓝牙设备...");
                    bleService.scanDevice(blutoothName);
                }
            }
        });

        //返回
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isSend){
                    Test2Activity.this.finish();
                }else{
                    dialogView = new DialogView(mContext, "设备正在传输数据，暂不能离开当前页面", "知道了",null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                        }
                    }, null);
                    dialogView.show();
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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            bleService = ((BleService.LocalBinder) rawBinder).getService();
            mBtAdapter = bleService.createBluetoothAdapter();
            SendBleDataManager.getInstance().init(bleService);
            //判断蓝牙是否打开
            BleUtils.isEnabled(Test2Activity.this,mBtAdapter);

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
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(Test2Activity.this,"扫描并连接蓝牙设备...");
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
                            isSend=false;
                            DialogUtils.showProgress(Test2Activity.this,"蓝牙连接中...");
                            mHandler.postDelayed(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
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
                    DialogUtils.showProgress(Test2Activity.this,"发送命令中...");
                    SendBleDataManager.getInstance().sendData("GDBLEGPRSSENDDATA",true);
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    DialogUtils.closeProgress();
                    String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    showData(data);

                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "接收数据超时！", "确定",null, new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            isSend=false;
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            isSend=false;
                            DialogUtils.showProgress(Test2Activity.this,"发送命令中...");
                            SendBleDataManager.getInstance().sendData("GDBLEGPRSSENDDATA",true);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                    DialogUtils.closeProgress();
                    isSend=false;
                    showToastView("设备回执数据异常");
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 展示设备回执的数据
     * @param data
     */
    private void showData(String data){
        if(data.startsWith("GDBLEGPRSSENDDATA-11.") || data.startsWith("GDBLEGPRSSENDDATA-12.")){
            isSend=false;
        }else{
            isSend=true;
        }
        if(!isNewDevice){
            tvStatus.setText(data);
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-01.")){
            tvStatus.setText("正在搜索无线信号");
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-02.")){
            tvStatus.setText("正在搜索无线信号");
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-03.")){
            tvStatus.setText("未安装SIM卡或SIM卡安装错误,请检查!");
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-04.")){
            tvStatus.setText("SIM卡正常,但无法找到无线信号. 请用手机或其它设备测试信号");
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-05.")){
            tvStatus.setText("已成功找到无线信号信号质量： "+getPercentage(data));
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-06.")){
            tvStatus.setText("正在登录 internet网络信号质量： "+getPercentage(data));
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-07.")){
            tvStatus.setText("无法登录 internet 网络");
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-08.")){
            tvStatus.setText("正在连接数据服务器信号质量： "+getPercentage(data));
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-09.")){
            tvStatus.setText("连接数据服务器失败");
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-10.")){
            tvStatus.setText("正在发送数据");
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-11.")){
            tvStatus.setText("发送数据成功");
            return;
        }
        if(data.startsWith("GDBLEGPRSSENDDATA-12.")){
            tvStatus.setText("发送数据失败");
            return;
        }
    }


    /**
     * 获取百分比数据
     * @param data
     */
    private String getPercentage(String data){
        String[] strs=data.split("\\.");
        if(strs==null && strs.length==1){
            return null;
        }
        final int num=Integer.parseInt(strs[1]);
        if(num>=0 && num<=20){
            return strs[1]+"%(很弱)";
        }
        if(num>=21 && num<=31){
            return strs[1]+"%(较弱)";
        }
        if(num>=32 && num<=45){
            return strs[1]+"%(偏弱)";
        }
        if(num>=46 && num<=58){
            return strs[1]+"%(一般)";
        }
        if(num>=59 && num<=69){
            return strs[1]+"%(良好)";
        }
        return strs[1]+"%(较好)";
    }


    /**
     * EventBus注解
     */
    @Subscribe
    public void onEvent(EventType eventType) {
        switch (eventType.getStatus()) {
            case EventStatus.SHOW_DEVICE_DATA:

                 break;
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(!isSend){
                Test2Activity.this.finish();
            }else{
                dialogView = new DialogView(mContext, "设备正在传输数据，暂不能离开当前页面", "知道了",null, new View.OnClickListener() {
                    public void onClick(View v) {
                        dialogView.dismiss();
                    }
                }, null);
                dialogView.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(null!=bleService){
            bleService.disconnect();
        }
        unbindService(mServiceConnection);
    }
}
