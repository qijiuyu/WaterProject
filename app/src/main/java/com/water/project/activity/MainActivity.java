package com.water.project.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.SendBleDataManager;
public class MainActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout linearLayout1,linearLayout2,linearLayout3;
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
    }


    /**
     * 初始化控件
     */
    private void initView(){
        linearLayout1=(LinearLayout)findViewById(R.id.lin_am1);
        linearLayout2=(LinearLayout)findViewById(R.id.lin_am2);
        linearLayout3=(LinearLayout)findViewById(R.id.lin_am3);
        TextView tvAbout=(TextView)findViewById(R.id.tv_about);
        tvAbout.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        tvAbout.setOnClickListener(this);
        findViewById(R.id.tv_am_scan).setOnClickListener(this);
        findViewById(R.id.lin_am_setting).setOnClickListener(this);
        findViewById(R.id.lin_am_data).setOnClickListener(this);
        findViewById(R.id.lin_am_yan).setOnClickListener(this);
        findViewById(R.id.lin_am_net).setOnClickListener(this);
        findViewById(R.id.lin_am_record).setOnClickListener(this);
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
                Intent intent=new Intent(this,SearchBleActivity.class);
                startActivityForResult(intent,0x001);
//                Intent intent=new Intent(this,New_SettingActivity.class);
//                startActivityForResult(intent,0x001);
                break;
            //参数设置
            case R.id.lin_am_setting:
                 final int code=getVersion();
                 if(code==1){
                     setClass(SettingActivity.class);
                 }else{
                     setClass(New_SettingActivity.class);
                 }
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
            //读取设备记录
            case R.id.lin_am_record:
                setClass(GetRecordActivity.class);
                  break;
            //关于我们
            case R.id.tv_about:
                 setClass(AboutActivity.class);
                 break;
            default:
                break;
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0x001){
            //读取版本信息成功
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            final int code=getVersion();
            if(code!=1){
                linearLayout3.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 获取设备版本
     * @return
     */
    private int getVersion(){
        String version=SPUtil.getInstance(this).getString(SPUtil.DEVICE_VERSION);
        if(TextUtils.isEmpty(version)){
            return 1;
        }
        String[] vs=version.split("-");
        if(null==vs || vs.length==0){
            return 1;
        }
        if(version.startsWith("GDBBRGDsender")){
            return 1;
        }
        if(version.startsWith("GDBBRZKGD2000")){
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