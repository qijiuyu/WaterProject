package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.water.project.R;
import com.water.project.adapter.BleItemAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.BindService;
import com.water.project.bean.Ble;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.service.BleService;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.BleObject;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import com.water.project.view.RippleBackground;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
        //注册EventBus
        EventBus.getDefault().register(this);
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
    private BleService bleService;
    private void scanBle(){
        bleService= BleObject.getInstance().getBleService(this, new BindService() {
            @Override
            public void onSuccess() {
                scanBle();
            }
        });
        if(bleService==null){
            return;
        }
        rippleBackground.startRippleAnimation();
        bleService.scanDevice(null);
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
        myIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);//接收到了回执的数据
        myIntentFilter.addAction(BleService.ACTION_INTERACTION_TIMEOUT);//发送命令超时
        myIntentFilter.addAction(BleService.ACTION_SEND_DATA_FAIL);//发送数据失败
        myIntentFilter.addAction(BleService.ACTION_GET_DATA_ERROR);//回执error数据
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
                    if(!TextUtils.isEmpty(bleMap.get(bleMac))){
                        return;
                    }
                    bleMap.put(bleMac,bleMac);
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
                     if(bleList.size()==0){
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
                                     bleService.connect(SearchBleActivity.this.ble.getBleMac());
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
                                             DialogUtils.showProgress(SearchBleActivity.this,"蓝牙连接中...");
                                             bleService.connect(SearchBleActivity.this.ble.getBleMac());
                                         }
                                     },100);
                                 }
                             }, null);
                             dialogView.show();
                         }
                     }
                     DialogUtils.closeProgress();
                     showToastView("蓝牙连接断开！");
                     break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                     //读取设备版本号
                     redVersion();
                     break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    DialogUtils.closeProgress();
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    //存储版本信息
                    SPUtil.getInstance(SearchBleActivity.this).addString(SPUtil.DEVICE_VERSION,data);
                    SearchBleActivity.this.setResult(0x001,new Intent());
                    SearchBleActivity.this.finish();
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
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
                    DialogUtils.closeProgress();
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
                    DialogUtils.closeProgress();
                    showToastView("设备回执数据异常");
                    break;
                default:
                     break;
            }

        }
    };


    /**
     * EventBus注解
     */
    @Subscribe
    public void onEvent(EventType eventType){
        switch (eventType.getStatus()){
            //去连接蓝牙
            case EventStatus.CONNCATION_BLE:
                  final Ble ble= (Ble) eventType.getObject();
                  if(null==ble){
                      return;
                  }
                  MyApplication.spUtil.addObject(SPUtil.BLE_DEVICE,ble);
                  DialogUtils.showProgress(SearchBleActivity.this,"蓝牙连接中...");
                  bleService.connect(ble.getBleMac());
                  break;
            default:
                break;
        }
    }


    /**
     * 读取设备版本号
     */
    private void redVersion(){
        DialogUtils.showProgress(SearchBleActivity.this,"正在读取版本号...");
        SendBleStr.sendBleData(this,BleContant.RED_DEVICE_VERSION);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        bleService.stopScan();
        unregisterReceiver(mBroadcastReceiver);
    }
}
