package com.water.project.activity.menu6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.adapter.BAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.BindService;
import com.water.project.bean.Ble;
import com.water.project.presenter.SendDataPersenter;
import com.water.project.service.BleService;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.FileUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.BleObject;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import com.water.project.view.MeasureListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 读取北斗卫星通讯信号强度
 */
public class RedBDSignalActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    @BindView(R.id.listView)
    MeasureListView listView;
    //MVP对象
    private SendDataPersenter sendDataPersenter;
    //下发命令的编号
    private int SEND_STATUS;
    private DialogView dialogView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_red_bd_signal);
        ButterKnife.bind(this);
        initView();
        //注册广播
        register();
        sendData(BleContant.RED_BEI_DOU_XIN_HAO_QIANG_DU);
    }

    /**
     * 初始化
     */
    private void initView(){
        //实例化MVP
        sendDataPersenter = new SendDataPersenter(this);
        tvHead.setText("读取北斗卫星信号强度");
    }

    @OnClick({R.id.lin_back, R.id.tv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                 finish();
                break;
            case R.id.tv_send:
                sendData(BleContant.RED_BEI_DOU_XIN_HAO_QIANG_DU);
                break;
            default:
                break;
        }
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
        //如果蓝牙连接断开，就扫描重连
        if (bleService.connectionState == bleService.STATE_DISCONNECTED) {
            //扫描并重连蓝牙
            final Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
            if (null != ble) {
                DialogUtils.showProgress(RedBDSignalActivity.this, "扫描并连接蓝牙设备...");
                bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        DialogUtils.showProgress(RedBDSignalActivity.this, "正在读取信号强度,请稍候...");
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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    sendDataPersenter.resumeScan(SEND_STATUS);
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    sendDataPersenter.bleDisConnect(bleService);
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    if (SEND_STATUS == BleContant.NOT_SEND_DATA) {
                        showToastView("蓝牙连接成功！");
                    } else {
                        sendData(SEND_STATUS);
                    }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    DialogUtils.closeProgress();
                    String data = intent.getStringExtra(BleService.ACTION_EXTRA_DATA);

                    try {

                        if(data.endsWith(">ERR")){
                            dialogView = new DialogView(dialogView,activity, "北斗通讯部分出现故障，请联系维护人员!","好的", null, null, null);
                            dialogView.show();
                        }else{

                            BuglyUtils.uploadBleMsg("北斗信号："+data);
                            String filePath = FileUtils.createFile("北斗信号数据.txt", data);

                            data=data.replace("GDBDSQ","").replace(">OK", "");
                            //显示信号列表
                            String[] strs=data.split(",");
                            BAdapter bAdapter=new BAdapter(activity,strs);
                            listView.setAdapter(bAdapter);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    sendDataPersenter.timeOut(SEND_STATUS);
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    sendDataPersenter.sendCmdFail(SEND_STATUS);
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                    DialogUtils.closeProgress();
                    showToastView("设备回执数据异常！");
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
