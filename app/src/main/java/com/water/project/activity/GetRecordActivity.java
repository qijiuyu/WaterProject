package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.menu5.CopyDataActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.presenter.GetRecordPersenter;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.FileUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 读取设备记录
 * Created by Administrator on 2019/10/10.
 */

public class GetRecordActivity extends BaseActivity {

    private DialogView dialogView;
    private Handler mHandler = new Handler();
    //当前页面是否在显示
    private boolean isShowActivity=true;
    private StringBuffer sb;
    //下发命令的编号
    private int SEND_STATUS;
    //需要读取的三类数据
    private String red1,red2;
    public StringBuffer red3=new StringBuffer();
    private GetRecordPersenter persenter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_get_record);
        ButterKnife.bind(this);
        register();//注册广播

        TextView tvHead =findViewById(R.id.tv_head);
        tvHead.setText("数据记录和状态记录");
    }


    @OnClick({R.id.lin_back, R.id.tv_red, R.id.tv_copy,R.id.tv_red_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                 finish();
                break;
            //读取GPRS发送数据过程状态记录
            case R.id.tv_red:
                sb=new StringBuffer();
                sendData(BleContant.RED_DEVICE_RECOFD);
                break;
            //数据拷贝
            case R.id.tv_copy:
                setClass(CopyDataActivity.class);
                break;
            //读取设备数据记录
            case R.id.tv_red_record:
                 persenter=new GetRecordPersenter(this);
                 red3=new StringBuffer();
//                 sendData(BleContant.COPY_DEVICE_DATA);
                persenter.showDialogRed3("GDRECORDXXR0064800,140801120300, 140801121800, 0003, 140801122025");
                 break;
            default:
                break;
        }
    }


    /**
     * 发送蓝牙命令
     */
    public void sendData(int status) {
        SEND_STATUS=status;
        //判断蓝牙是否打开
        if (!BleUtils.isEnabled(GetRecordActivity.this, MainActivity.mBtAdapter)) {
            return;
        }
        DialogUtils.showProgress(GetRecordActivity.this, "正在读取数据记录...");
        //如果蓝牙连接断开，就扫描重连
        if (MainActivity.bleService.connectionState == MainActivity.bleService.STATE_DISCONNECTED) {
            //扫描并重连蓝牙
            final Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
            if (null != ble) {
                DialogUtils.showProgress(GetRecordActivity.this, "扫描并连接蓝牙设备...");
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


    //true：表示可以重发上条读取的命令
    private boolean isResumeRed=true;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(!isShowActivity){
                return;
            }
            switch (intent.getAction()) {
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(dialogView,mContext, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描", "取消", new View.OnClickListener() {
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
                    dialogView = new DialogView(dialogView,mContext, "蓝牙连接断开，请靠近设备进行连接!", "重新连接", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            DialogUtils.showProgress(GetRecordActivity.this, "蓝牙连接中...");
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
                    String data = intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    if(SEND_STATUS==BleContant.RED_DEVICE_RECOFD){
                        sb.append(data);
                        if(!data.endsWith(">OK")){
                            return;
                        }
                        DialogUtils.closeProgress();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final String fileName = data.substring(9, 24) + "_" + sdf.format(new Date()) + ".txt";
                        String filePath = FileUtils.createFile(fileName, sb.toString());
                        dialogView = new DialogView(dialogView,mContext, "数据.txt文件已创建成功，目录是：" + filePath, "确定", null, new View.OnClickListener() {
                            public void onClick(View v) {
                                dialogView.dismiss();
                            }
                        }, null);
                        dialogView.show();
                    }else{
                        switch (SEND_STATUS){
                            case BleContant.COPY_DEVICE_DATA:
                                red1=data;
                                //发送获取统一编码的命令
                                sendData(BleContant.COPY_DEVICE_ID);
                                break;
                            case BleContant.COPY_DEVICE_ID:
                                 DialogUtils.closeProgress();//此处关闭loding框
                                 red2=data;
                                 persenter.showDialogRed3(red1);
                                 break;
                            case BleContant.RED_DEVICE_DATA_BY_TIME:
                                 if(data.length()>256){
                                    data=data.substring(18,data.length()-8);
                                 }
                                //如果长度不够就重新发送
                                if(data.length()%256!=0){
                                    if(isResumeRed){
                                        isResumeRed=false;
                                        sendData(BleContant.RED_DEVICE_DATA_BY_TIME);
                                    }else{
                                        //关闭读取时的进度框
                                        persenter.closeTripDialog();
                                        dialogView = new DialogView(dialogView,GetRecordActivity.this, "读取到的数据长度不是128的倍数","知道了",null, new View.OnClickListener() {
                                            public void onClick(View v) {
                                                isResumeRed=true;
                                                dialogView.dismiss();
                                            }
                                        }, null);
                                        dialogView.show();
                                    }
                                    return;
                                }

                                //追加第三条结果数据
                                red3.append(data);

                                if(persenter.setRed3Cmd()){
                                    sendData(BleContant.RED_DEVICE_DATA_BY_TIME);
                                }else{
                                    DialogUtils.closeProgress();
                                    persenter.showRedComplete(red3.toString());
                                }
                                 break;
                             default:
                                 break;
                        }
                    }

                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(dialogView,mContext, "接收数据超时！", "重试", "取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogView.dismiss();
                            sendData(SEND_STATUS);
                        }
                    }, null);
                    dialogView.show();
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    dialogView = new DialogView(dialogView,mContext, "下发命令失败！", "重试", "取消", new View.OnClickListener() {
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
    protected void onResume() {
        super.onResume();
        isShowActivity=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isShowActivity=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
