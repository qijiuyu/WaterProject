package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import com.water.project.view.RippleBackground;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜寻蓝牙
 */
public class SearchBleActivity extends BaseActivity {

    private RippleBackground rippleBackground;
    private ListView listView;
    private BleItemAdapter bleItemAdapter;
    //存储扫描到的蓝牙名称
    private List<Ble> bleList=new ArrayList<>();
    private Map<String ,String> bleMap=new HashMap<>();
    private DialogView dialogView;
    private Handler mHandler=new Handler();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ble);
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
        TextView tvRight=(TextView)findViewById(R.id.tv_right);
        tvRight.setText("重新扫描");
        listView=(ListView)findViewById(R.id.list_asb);
        bleItemAdapter=new BleItemAdapter(mContext,bleList);
        listView.setAdapter(bleItemAdapter);
        bleItemAdapter.setCallBack(bleConCallBack);
        rippleBackground=(RippleBackground)findViewById(R.id.content);
        tvRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bleList.clear();
                bleMap.clear();
                if(null!=bleItemAdapter){
                    bleItemAdapter.notifyDataSetChanged();
                }
                scanBle();
            }
        });
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
        rippleBackground.startRippleAnimation();
        MainActivity.bleService.scanDevice(null);
    }


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BleService.ACTION_SCAN_SUCCESS);//扫描到蓝牙设备了
        myIntentFilter.addAction(BleService.ACTION_SCAM_DEVICE_END);//30秒扫描完毕
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
                    if(!TextUtils.isEmpty(bleMap.get(bleName))){
                        return;
                    }
                    bleMap.put(bleName,bleName);
                    ble=new Ble(bleName,bleMac);
                    if(bleName.contains("ZKGD")){
                        bleList.add(ble);
                        bleItemAdapter.notifyDataSetChanged();
                    }
                     break;
                //30秒扫描完毕
                case BleService.ACTION_SCAM_DEVICE_END:
                     //停止动态效果
                     rippleBackground.stopRippleAnimation();
                     boolean b=false;
                     for (int i=0;i<bleList.size();i++){
                          if(bleList.get(i).getBleName().contains("ZKGD")){
                              b=true;
                              break;
                          }
                     }
                     if(!b){
                         dialogView = new DialogView(mContext, "未搜索到到蓝牙设备!","知道了", null, new View.OnClickListener() {
                             public void onClick(View v) {
                                 dialogView.dismiss();
                             }
                         }, null);
                         dialogView.show();
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
                             return;
                         }else{
                             isConnect=true;
                             dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
                                 public void onClick(View v) {
                                     dialogView.dismiss();
                                     mHandler.postDelayed(new Runnable() {
                                         public void run() {
                                             showProgress("蓝牙连接中...");
                                             MainActivity.bleService.connect(SearchBleActivity.this.ble.getBleMac());
                                         }
                                     },100);
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
                     //读取设备版本号
                     redVersion();
                     break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    clearTask();
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    //存在版本信息
                    SPUtil.getInstance(SearchBleActivity.this).addString(SPUtil.DEVICE_VERSION,data);
                    SearchBleActivity.this.finish();
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    clearTask();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            //读取设备版本号
                            redVersion();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    clearTask();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            //读取设备版本号
                            redVersion();
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                    clearTask();
                    showToastView("设备回执数据异常");
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


    /**
     * 读取设备版本号
     */
    private void redVersion(){
        showProgress("正在读取版本号...");
        SendBleStr.sendBleData(BleContant.RED_DEVICE_VERSION,1);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.bleService.stopScan();
        unregisterReceiver(mBroadcastReceiver);
    }
}
