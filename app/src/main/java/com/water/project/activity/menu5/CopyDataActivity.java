package com.water.project.activity.menu5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.activity.MainActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.presenter.CopyDataPersenter;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 读取设备数据，发送到其他设备
 * Created by Administrator on 2020/3/18.
 */
public class CopyDataActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    private DialogView dialogView;
    /**
     * 1：读取数据
     * 2：写入数据
     */
    private int type=1;
    //需要读取的三类数据
    private String red1,red2,red3;
    //下发命令的编号
    private int SEND_STATUS;
    //蓝牙名称
    private String bleName="ZKGDBluetooth";
    private CopyDataPersenter copyDataPersenter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_data);
        ButterKnife.bind(this);
        copyDataPersenter=new CopyDataPersenter(this);
        //注册EventBus
        EventBus.getDefault().register(this);
        tvHead.setText("数据记录拷贝");

        //注册广播
        register();
    }

    @OnClick({R.id.lin_back, R.id.tv_red, R.id.tv_wirte})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                finish();
                break;
            //读取数据
            case R.id.tv_red:
                type=1;
                Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
                bleName=ble.getBleName();
                sendData(BleContant.COPY_DEVICE_DATA);
//                copyDataPersenter.setRed3Cmd("GDRECORDXXR0064800,140808120300,140810120300,0030,140801122025");
//                copyDataPersenter.showTripDialog("GDRECORDXXR0064800,140808120300,140810120300,0030,140801122025",null);
//                copyDataPersenter.showRedComplete(null);
                break;
            //写入数据
            case R.id.tv_wirte:
                type=2;
                bleName="ZKGDBluetooth";
                sendData(BleContant.WRITE_NEW_DEVICE_CMD);
                break;
            default:
                break;
        }
    }

    /**
     * 发送蓝牙命令
     */
    private void sendData(int SEND_STATUS) {
        this.SEND_STATUS=SEND_STATUS;
        //如果蓝牙连接断开，就扫描重连
        if (MainActivity.bleService.connectionState == MainActivity.bleService.STATE_DISCONNECTED) {
            DialogUtils.showProgress(CopyDataActivity.this, "扫描并连接蓝牙设备...");
            MainActivity.bleService.scanDevice(bleName);
            return;
        }
        //开始发送读取蓝牙命令
        if(type==1){
            switch (SEND_STATUS){
                case BleContant.COPY_DEVICE_DATA:
                    DialogUtils.showProgress(CopyDataActivity.this, "读取原始设备数据记录信息指令");
                    break;
                case BleContant.COPY_DEVICE_ID:
                    DialogUtils.showProgress(CopyDataActivity.this, "读取原始设备的统一编码");
                    break;
                case BleContant.RED_DEVICE_DATA_BY_TIME:
                     copyDataPersenter.showTripDialog(red3);
                     break;
                default:
                    break;
            }
        }

        //发送写入新设备的命令
        if(type==2){
            switch (SEND_STATUS){
                case BleContant.WRITE_NEW_DEVICE_CMD:
                      DialogUtils.showProgress(CopyDataActivity.this, "蓝牙APP启动拷贝数据记录命令");
                      break;
                default:
                    break;
            }
        }

        SendBleStr.sendBleData(SEND_STATUS);
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
        myIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE2);//接收到了回执的数据
        myIntentFilter.addAction(BleService.ACTION_INTERACTION_TIMEOUT);//发送命令超时
        myIntentFilter.addAction(BleService.ACTION_SEND_DATA_FAIL);//发送数据失败
        myIntentFilter.addAction(BleService.ACTION_GET_DATA_ERROR);//回执error数据
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                     copyDataPersenter.resumeScan();
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    copyDataPersenter.bleDisConnect();
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    //发送蓝牙命令
                    sendBleCmd();
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    final String data=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    //处理设备回执的数据
                    getDeviceData(data);
                    break;
                //接收到了实时回执的数据
                case BleService.ACTION_DATA_AVAILABLE2:
                      if(SEND_STATUS==BleContant.RED_DEVICE_DATA_BY_TIME){
                          final String data2=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                          red3+=data2;
                          //实时更新提示框内容
                          copyDataPersenter.showTripDialog(red3);
                      }
                    break;
                //读取数据超时
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    copyDataPersenter.timeOut();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    if(type==1){
                        dialogView = new DialogView(mContext, "读取数据记录出现故障", "好的", null, new View.OnClickListener() {
                            public void onClick(View v) {
                                dialogView.dismiss();
                            }
                        }, null);
                        dialogView.show();
                    }
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
     * 发送蓝牙命令
     */
    private void sendBleCmd(){
        if(type==1){
            sendData(BleContant.COPY_DEVICE_DATA);
        }else{

        }
    }


    /**
     * 处理设备回执的数据
     * @param data
     */
    private void getDeviceData(String data){
        if(type==1){
            DialogUtils.closeProgress();
            switch (SEND_STATUS){
                case BleContant.COPY_DEVICE_DATA:
                      red1=data;
                      //发送获取统一编码的命令
                      sendData(BleContant.COPY_DEVICE_ID);
                      break;
                case BleContant.COPY_DEVICE_ID:
                      red2=data;
                      //发送设置根据时间段读取设备里面的数据
                      copyDataPersenter.setRed3Cmd(red1);
                      sendData(BleContant.RED_DEVICE_DATA_BY_TIME);
                      break;
                //读取设备数据结束了
                case BleContant.RED_DEVICE_DATA_BY_TIME:
                     boolean b=copyDataPersenter.setRed3Cmd(red1);
                     if(b){
                         sendData(BleContant.RED_DEVICE_DATA_BY_TIME);
                     }else{
                         copyDataPersenter.showRedComplete(red3);
                         //断开蓝牙连接
                         MainActivity.bleService.disconnect();
                     }
                      break;
                default:
                      break;
            }
        }

        if(type==2){
            switch (SEND_STATUS){
                case BleContant.WRITE_NEW_DEVICE_CMD:
                     if(data.endsWith(">OK")){
                        DialogUtils.showProgress(this,"等待设备读取数据中...");
                     }
                      break;
                default:
                    break;
            }
        }
    }


    /**
     * EventBus注解
     */
    @Subscribe
    public void onEvent(EventType eventType) {
        switch (eventType.getStatus()) {
            //发送命令
            case EventStatus.SEND_CHECK_MCD:
                sendData(SEND_STATUS);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
    }
}
