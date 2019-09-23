package com.water.project.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.new_version.New_SettingActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.SelectTime;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.utils.ble.SendBleDataManager;
import com.water.project.utils.photo.GlideImageLoader;
import com.water.project.view.DialogView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout linearLayout1,linearLayout2;
    // 按两次退出
    protected long exitTime = 0;
    //蓝牙参数
    public static BleService bleService = null;
    public static BluetoothAdapter mBtAdapter = null;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        //删除缓存
        deleteCache();
        initService();//注册蓝牙服务
        register();//注册广播
    }


    /**
     * 初始化控件
     */
    private void initView(){
        linearLayout1=(LinearLayout)findViewById(R.id.lin_am1);
        linearLayout2=(LinearLayout)findViewById(R.id.lin_am2);
        TextView tvAbout=(TextView)findViewById(R.id.tv_about);
        tvAbout.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        tvAbout.setOnClickListener(this);
        findViewById(R.id.tv_am_scan).setOnClickListener(this);
        findViewById(R.id.lin_am_setting).setOnClickListener(this);
        findViewById(R.id.lin_am_data).setOnClickListener(this);
        findViewById(R.id.lin_am_yan).setOnClickListener(this);
        findViewById(R.id.lin_am_net).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //扫描链接蓝牙
            case R.id.tv_am_scan:
                 setClass(SearchBleActivity.class);
                break;
            //参数设置
            case R.id.lin_am_setting:
                 final int code=getVersion();
//                 if(code==1){
//                     setClass(SettingActivity.class);
//                 }else{
                     setClass(New_SettingActivity.class);
//                 }
                 break;
            //网络shezhi
            case R.id.lin_am_net:
                 setClass(NetSettingActivity.class);
                 break;
            //实时数据
            case R.id.lin_am_data:
                 setClass(GetDataActivity.class);
                 break;
            //数据校验
            case R.id.lin_am_yan:
                 setClass(CheckActivity.class);
                 break;
            //关于我们
            case R.id.tv_about:
                 setClass(AboutActivity.class);
                 break;
            default:
                break;
        }
    }


    /**
     * 判断是否连接了蓝牙
     */
    private DialogView dialogView;
    private boolean isConnect(){
        if(MainActivity.bleService.connectionState==MainActivity.bleService.STATE_DISCONNECTED){
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null==ble){
                dialogView = new DialogView(mContext, "请回到首页去扫描并连接蓝牙！", "知道了",null, new View.OnClickListener() {
                    public void onClick(View v) {
                        dialogView.dismiss();
                    }
                }, null);
                dialogView.show();
                return false;
            }
        }
        return true;
    }


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_ENABLE_NOTIFICATION_SUCCES);//蓝牙初始化通道成功
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(null==intent){
                return;
            }
            switch (intent.getAction()){
                //蓝牙初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                     linearLayout1.setVisibility(View.VISIBLE);
                     linearLayout2.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

        }
    };


    /**
     * 获取设备版本
     * @return
     */
    private int getVersion(){
        String version=SPUtil.getInstance(this).getString(SPUtil.DEVICE_VERSION);
        if(TextUtils.isEmpty(version)){
            return 0;
        }
        String[] vs=version.split("-");
        if(null==vs || vs.length==0){
            return 0;
        }
        if(version.contains("GDsender")){
            return 1;
        }
        if(version.contains("ZKGD2000")){
            if(vs.length==2){
                return 1;
            }
            if(vs[2].contains("V1") || vs[2].contains("V2")){
                return 1;
            }
            return 2;
        }
        return 0;
    }


    /**
     * 删除缓存
     */
    private void deleteCache(){
        MyApplication.spUtil.removeAll();
    }

    // 按两次退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToastView("再按一次退出程序！");
                exitTime = System.currentTimeMillis();
            } else {
                bleService.disconnect();
                try {
                    unbindService(mServiceConnection);
                    unregisterReceiver(mBroadcastReceiver);
                }catch (Exception e){

                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                },200);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=bleService){
            bleService.disconnect();
        }
    }
}