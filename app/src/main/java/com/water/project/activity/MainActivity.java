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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.utils.ble.SendBleDataManager;
import com.water.project.utils.photo.GlideImageLoader;
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
//    private Banner banner;
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
//        startBanner();//加载轮播图片
        initService();//注册蓝牙服务
        register();//注册广播

    }


    /**
     * 初始化控件
     */
    private void initView(){
//        banner = (Banner) findViewById(R.id.banner);
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


//    private void startBanner(){
//        //设置banner样式
//        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
//        //设置图片加载器
//        banner.setImageLoader(new GlideImageLoader());
//        //设置图片集合
//        banner.setImages(imgList);
//        //设置banner动画效果
//        banner.setBannerAnimation(Transformer.DepthPage);
//        //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(titleList);
//        //设置自动轮播，默认为true
//        banner.isAutoPlay(true);
//        //设置轮播时间
//        banner.setDelayTime(7000);
//        //设置指示器位置（当banner模式中有指示器时）
//        banner.setIndicatorGravity(BannerConfig.CENTER);
//        //banner设置方法全部调用完毕时最后调用
//        banner.start();
//    }


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
                 setClass(SettingActivity.class);
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

    // 按两次退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToastView("再按一次退出程序！");
                exitTime = System.currentTimeMillis();
            } else {
                bleService.disconnect();
                unbindService(mServiceConnection);
                unregisterReceiver(mBroadcastReceiver);
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
        bleService.disconnect();
    }
}