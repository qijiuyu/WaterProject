package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.adapter.GetMoreDataAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.BindService;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.BleObject;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 获取多路实时数据
 */
public class GetMoreDataActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.tv_no)
    TextView tvNo;
    //下发命令的编号
    private int SEND_STATUS;

    //总路数
    private int road;
    //读取时的路数
    private int redRoad=1;
    //接收实时数据
    private List<String> list=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_get_more_data);
        ButterKnife.bind(this);
        initView();
        register();//注册广播
        sendData(BleContant.RED_CAIJI_ROAD); //发送蓝牙命令
    }


    /**
     * 初始化
     */
    private void initView(){
        tvHead.setText("多路实时数据");
    }

    @OnClick(R.id.lin_back)
    public void onViewClicked() {
        finish();
    }


    /**
     * 发送蓝牙命令
     */
    private BleService bleService;
    private void sendData(final int SEND_STATUS) {
        this.SEND_STATUS=SEND_STATUS;
        bleService= BleObject.getInstance().getBleService(this, new BindService() {
            @Override
            public void onSuccess() {
                sendData(SEND_STATUS);
            }
        });
        if(bleService==null){
            return;
        }
        if(SEND_STATUS==BleContant.RED_CAIJI_ROAD){
            DialogUtils.showProgress2(this,"正在读取路数数据");
        }else{
            DialogUtils.showProgress2(this,"正在读取第"+redRoad+"路实时数据...");
        }
        //如果蓝牙连接断开，就扫描重连
        if(bleService.connectionState==bleService.STATE_DISCONNECTED){
            //扫描并重连蓝牙
            final Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
            if(null!=ble){
                DialogUtils.showProgress(this,"扫描并连接蓝牙设备...");
                bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        SendBleStr.sendBleData(this,SEND_STATUS);
    }


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
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "蓝牙连接断开！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    sendData(SEND_STATUS);
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA).replace(">OK","");

                    if(SEND_STATUS==BleContant.RED_CAIJI_ROAD){
                        data=data.replace("GDMETERNUMR","");
                        String[] msg=data.split(",");
                        final int m=Integer.parseInt(msg[0]);
                        road=Integer.parseInt(msg[1]);
                        if(m==0){
                            //根据路数读取实时数据
                            SendBleStr.redTimeDataByRoad(redRoad);
                            sendData(BleContant.RED_TIME_DATA_BY_ROAD);
                        }else{
                            DialogUtils.closeProgress();
                            tvNo.setVisibility(View.VISIBLE);
                        }
                    }else{
                        list.add(data);
                        listView.setAdapter(new GetMoreDataAdapter(GetMoreDataActivity.this,list));
                        if(redRoad<road){
                            //根据路数读取实时数据
                            SendBleStr.redTimeDataByRoad(++redRoad);
                            sendData(BleContant.RED_TIME_DATA_BY_ROAD);
                        }else{
                            DialogUtils.closeProgress();
                        }
                    }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试","取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
