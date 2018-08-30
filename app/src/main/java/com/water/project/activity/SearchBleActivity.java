package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ListView;
import com.water.project.R;
import com.water.project.adapter.BleItemAdapter;
import com.water.project.bean.Ble;
import com.water.project.bean.BleConCallBack;
import com.water.project.service.BleService;
import com.water.project.utils.LogUtils;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.view.RippleBackground;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜寻蓝牙
 */
public class SearchBleActivity extends BaseActivity {

    private RippleBackground rippleBackground;
    private ListView listView;
    private BleItemAdapter bleItemAdapter;
    //存储扫描到的蓝牙名称
    private List<Ble> bleList=new ArrayList<>();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_search_ble);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_1fc37f);
        initView();
        register();//注册广播
        scanBle();//开始扫描蓝牙
    }


    /**
     * 初始化控件
     */
    private void initView(){
        listView=(ListView)findViewById(R.id.list_asb);
        rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
    }


    /**
     * 开始扫描蓝牙
     */
    private void scanBle(){
        if(null==MainActivity.bleService){
            return;
        }
        MainActivity.bleService.scanDevice();
    }


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_SCAN_SUCCESS);//扫描到蓝牙设备了
        myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);//蓝牙断开连接
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(null==intent){
                return;
            }
            switch (intent.getAction()){
                //扫描到蓝牙设备了
                case BleService.ACTION_SCAN_SUCCESS:
                    final String bleName=intent.getStringExtra("bleName");
                    final String bleMac=intent.getStringExtra("bleMac");
                    Ble ble=new Ble(bleName,bleMac);
                    if(bleName.contains("ZX-PARK")){
                        bleList.add(0,ble);
                    }else{
                        bleList.add(ble);
                    }
                    if(null==bleItemAdapter){
                        bleItemAdapter=new BleItemAdapter(mContext,bleList);
                        listView.setAdapter(bleItemAdapter);
                        bleItemAdapter.setCallBack(bleConCallBack);
                    }else{
                        bleItemAdapter.notifyDataSetChanged();
                    }
                     break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                     break;
                default:
                     break;
            }

        }
    };


    private BleConCallBack bleConCallBack=new BleConCallBack() {
        /**
         * 连接蓝牙
         * @param bleMac
         */
        public void connetion(String bleMac) {
            if(TextUtils.isEmpty(bleMac)){
                return;
            }
            showProgress("蓝牙连接中...");
            boolean isCon=MainActivity.bleService.connect(bleMac);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
