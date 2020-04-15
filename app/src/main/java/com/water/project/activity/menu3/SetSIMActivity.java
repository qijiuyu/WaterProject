package com.water.project.activity.menu3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.activity.MainActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 网络设置页面，如果返回北斗信息就进入新的页面
 * Created by Administrator on 2020/4/4.
 */
public class SetSIMActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    @BindView(R.id.et_sim)
    EditText etSim;
    private String totalData;
    //中心号码在字符串中的位置
    private int cenIndex,endIndex;
    private DialogView dialogView;
    private Handler mHandler = new Handler();
    //下发命令的编号
    private int SEND_STATUS;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sim);
        ButterKnife.bind(this);

        tvHead.setText("数据接收中心号码");
        totalData=getIntent().getStringExtra("data");
        //显示中心号码数据
        showMobile();

        register();//注册广播
    }


    /**
     * 显示中心号码数据
     */
    private void showMobile(){
        cenIndex=totalData.indexOf("CEN");
        endIndex=totalData.subSequence(cenIndex, totalData.length()).toString().indexOf(",");
        etSim.setText(totalData.substring(cenIndex, cenIndex+endIndex).replace("CEN",""));
    }

    @OnClick({R.id.lin_back, R.id.tv_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                finish();
                break;
            //修改
            case R.id.tv_update:
                String mobile=etSim.getText().toString().trim();
                if(mobile.length()==12){
                    ToastUtil.showLong("中心号码长度只支持11位，或者13位");
                    return;
                }
                SendBleStr.setCenterSIM(totalData,mobile,cenIndex,endIndex);
                sendData(BleContant.SET_CENTER_MOBILE);
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
        //判断蓝牙是否打开
        if (!BleUtils.isEnabled(SetSIMActivity.this, MainActivity.mBtAdapter)) {
            return;
        }
        if(SEND_STATUS==BleContant.SET_CENTER_MOBILE){
            DialogUtils.showProgress(SetSIMActivity.this, "正在设置中心号码");
        }else{
            DialogUtils.showProgress(SetSIMActivity.this, "正在读取中心号码...");
        }
        //如果蓝牙连接断开，就扫描重连
        if (MainActivity.bleService.connectionState == MainActivity.bleService.STATE_DISCONNECTED) {
            //扫描并重连蓝牙
            final Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
            if (null != ble) {
                DialogUtils.showProgress(SetSIMActivity.this, "扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
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
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "蓝牙连接断开，请靠近设备进行连接!", "重新连接", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(SetSIMActivity.this, "蓝牙连接中...");
                            mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
                                    MainActivity.bleService.connect(ble.getBleMac());
                                }
                            }, 100);
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
                    if(SEND_STATUS==BleContant.SET_CENTER_MOBILE){
                        ToastUtil.showLong("中心号码设置成功");
                        //重新读取中心号码
                        sendData(BleContant.SEND_GET_CODE_PHONE);
                    }else{
                        DialogUtils.closeProgress();
                        totalData=intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                        //显示中心号码数据
                        showMobile();
                    }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "接收数据超时！", "重试", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(mContext, "下发命令失败！", "重试", "取消", new View.OnClickListener() {
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