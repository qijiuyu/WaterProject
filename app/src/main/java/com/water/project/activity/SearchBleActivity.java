package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.adapter.BleItemAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.BleConCallBack;
import com.water.project.service.BleService;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;
import com.water.project.view.DialogView;
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
    private DialogView dialogView;
    private Handler mHandler=new Handler();
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
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("查找蓝牙");
        listView=(ListView)findViewById(R.id.list_asb);
        rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBleActivity.this.finish();
            }
        });
    }


    /**
     * 开始扫描蓝牙
     */
    private void scanBle(){
        if(null==MainActivity.bleService){
            return;
        }
        MainActivity.bleService.scanDevice(null);
    }


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_SCAN_SUCCESS);//扫描到蓝牙设备了
        myIntentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);//蓝牙断开连接
        myIntentFilter.addAction(BleService.ACTION_ENABLE_NOTIFICATION_SUCCES);//蓝牙初始化通道成功
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private boolean isConnect = true;
    private Ble ble;
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
                    ble=new Ble(bleName,bleMac);
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
                     final int status=intent.getIntExtra("status",0);
                     if(status!=0){
                         ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                         if (isConnect) {
                             isConnect=false;
                             LogUtils.e("重新连接一次蓝牙!");
                             mHandler.postDelayed(new Runnable() {
                                 public void run() {
                                     MainActivity.bleService.connect(SearchBleActivity.this.ble.getBleMac());
                                 }
                             },100);
                         }else{
                             isConnect=true;
                             dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                                 public void onClick(View v) {
                                     dialogView.dismiss();
                                     showProgress("蓝牙连接中...");
                                     MainActivity.bleService.connect(SearchBleActivity.this.ble.getBleMac());
                                 }
                             }, null);
                             dialogView.show();
                         }
                     }
                     clearTask();
                     showToastView("蓝牙连接断开！");
                     break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                     clearTask();
                     showToastView("蓝牙连接成功！");
                     SearchBleActivity.this.finish();
                     break;
                default:
                     break;
            }

        }
    };


    private BleConCallBack bleConCallBack=new BleConCallBack() {
        /**
         * 连接蓝牙
         * @param ble
         */
        public void connetion(Ble ble) {
            if(null==ble){
                return;
            }
            MyApplication.spUtil.addObject(SPUtil.BLE_DEVICE,ble);
            showProgress("蓝牙连接中...");
            boolean isCon=MainActivity.bleService.connect(ble.getBleMac());
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
