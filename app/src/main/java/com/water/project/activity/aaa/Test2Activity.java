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
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.adapter.Test2Adapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.FileUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.SendBleDataManager;
import com.water.project.view.DialogView;

import java.util.ArrayList;
import java.util.Calendar;
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
    private ListView listView;
    private Test2Adapter test2Adapter;
    /**
     * true：正在接受数据，不能离开界面
     * false：反之
     */
    private boolean isSend=false;
    private List<String> list=new ArrayList<>();
    private boolean isNewDevice=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initService();
        register();

        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("发送数据");
        final EditText etName=(EditText)findViewById(R.id.et_name);
        final EditText etVersion=findViewById(R.id.et_version);
        listView=findViewById(R.id.listView);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
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


        test2Adapter=new Test2Adapter(this,list);
        listView.setAdapter(test2Adapter);

        List<String> name=new ArrayList<>();
        name.add("GDBLEGPRSSENDDATA-1.00");
        name.add("GDBLEGPRSSENDDATA-2.00");
        name.add("GDBLEGPRSSENDDATA-3.00");
        name.add("GDBLEGPRSSENDDATA-4.00");
        name.add("GDBLEGPRSSENDDATA-5.20");
        name.add("GDBLEGPRSSENDDATA-6.40");
        name.add("GDBLEGPRSSENDDATA-7.00");
        name.add("GDBLEGPRSSENDDATA-8.60");
        name.add("GDBLEGPRSSENDDATA-9.00");
        name.add("GDBLEGPRSSENDDATA-10.00");
        name.add("GDBLEGPRSSENDDATA-11.00");
        name.add("GDBLEGPRSSENDDATA-12.00");

        for (int i=0;i<name.size();i++){
             showData(name.get(i));
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
                    SendBleDataManager.getInstance().sendData("GDBLEGPRSSENDDATA");
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
                            SendBleDataManager.getInstance().sendData("GDBLEGPRSSENDDATA");
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
        if(data.equals("GDBLEGPRSSENDDATA-11.00") || data.equals("GDBLEGPRSSENDDATA-12.00")){
            isSend=false;
        }else{
            isSend=true;
        }
        if(!isNewDevice){
            list.add(data);
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-1.00")){
            list.add("正在搜索无线信号");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-2.00")){
            list.add("正在搜索无线信号");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-3.00")){
            list.add("未安装SIM卡或SIM卡安装错误,请检查!");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-4.00")){
            list.add("SIM卡正常,但无法找到无线信号. 请用手机或其它设备测试信号");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.contains("GDBLEGPRSSENDDATA-5.")){
            list.add("已成功找到无线信号信号质量： "+getPercentage(data));
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.contains("GDBLEGPRSSENDDATA-6.")){
            list.add("正在登录 internet网络信号质量： "+getPercentage(data));
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-7.00")){
            list.add("无法登录 internet 网络");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.contains("GDBLEGPRSSENDDATA-8.")){
            list.add("正在连接数据服务器信号质量： "+getPercentage(data));
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-9.00")){
            list.add("连接数据服务器失败");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-10.00")){
            list.add("正在发送数据");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-11.00")){
            list.add("发送数据成功");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
            return;
        }
        if(data.equals("GDBLEGPRSSENDDATA-12.00")){
            list.add("发送数据失败");
            test2Adapter.notifyDataSetChanged();
            listView.setSelection(list.size()-1);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=bleService){
            bleService.disconnect();
        }
        unbindService(mServiceConnection);
    }
}
