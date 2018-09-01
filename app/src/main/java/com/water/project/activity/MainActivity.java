package com.water.project.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout linearLayout1,linearLayout2;
    // 按两次退出
    protected long exitTime = 0;
    //蓝牙参数
    public static BleService bleService = null;
    public BluetoothAdapter mBtAdapter = null;
    private Banner banner;
    //设置图片资源:url或本地资源
    private List<Integer> imgList=new ArrayList<>();
    //设置图片标题:自动对应
    private List<String> titleList=new ArrayList<>();
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
        startBanner();//加载轮播图片
        initService();//注册蓝牙服务
        register();//注册广播

    }


    /**
     * 初始化控件
     */
    private void initView(){
        banner = (Banner) findViewById(R.id.banner);
        linearLayout1=(LinearLayout)findViewById(R.id.lin_am1);
        linearLayout2=(LinearLayout)findViewById(R.id.lin_am2);
        imgList.add(R.mipmap.one);
        imgList.add(R.mipmap.two);
        imgList.add(R.mipmap.three);
        titleList.add("十大星级品牌联盟，全场2折起");
        titleList.add("嗨购5折不要停");
        titleList.add("全场2折起");
        findViewById(R.id.tv_am_scan).setOnClickListener(this);
        findViewById(R.id.tv_am_setting).setOnClickListener(this);
    }


    private void startBanner(){
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(imgList);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titleList);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
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
            case R.id.tv_am_setting:
                 setClass(SettingActivity.class);
                 break;
        }
    }


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_ENABLE_NOTIFICATION_SUCCES);//扫描到蓝牙设备了
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
                unbindService(mServiceConnection);
                unregisterReceiver(mBroadcastReceiver);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}





//        new SlideDateTimePicker.Builder(getSupportFragmentManager())
//                .setListener(listener)
//                .setInitialDate(new Date())
//                //.setMinDate(minDate)
//                //.setMaxDate(maxDate)
//                //.setIs24HourTime(true)
//                //.setTheme(SlideDateTimePicker.HOLO_DARK)
//                //.setIndicatorColor(Color.parseColor("#990000"))
//                .build()
//                .show();

//    private SlideDateTimeListener listener = new SlideDateTimeListener() {
//
//        @Override
//        public void onDateTimeSet(Date date)
//        {
//            Toast.makeText(MainActivity.this,
//                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
//        }
//
//        // Optional cancel listener
//        @Override
//        public void onDateTimeCancel()
//        {
//            Toast.makeText(MainActivity.this,
//                    "Canceled", Toast.LENGTH_SHORT).show();
//        }
//    };