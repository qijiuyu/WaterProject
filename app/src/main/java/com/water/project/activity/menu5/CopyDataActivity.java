package com.water.project.activity.menu5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
import com.water.project.utils.DialogUtils;
import com.water.project.utils.FileUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
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
    private String red1,red2;
    private StringBuffer red3=new StringBuffer();
    private StringBuffer saveSD=new StringBuffer();
    //下发命令的编号
    private int SEND_STATUS;
    //蓝牙名称
    private String bleName="ZKGDBluetooth";
    private CopyDataPersenter copyDataPersenter;
    private Handler handler=new Handler();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_data);
        ButterKnife.bind(this);
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
                copyDataPersenter=new CopyDataPersenter(this);
                red3.delete(0,red3.length());
                saveSD.delete(0,saveSD.length());
                type=1;
                Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
                bleName=ble.getBleName();
                sendData(BleContant.COPY_DEVICE_DATA);
                break;
            //写入数据
            case R.id.tv_wirte:
                copyDataPersenter.writeArray=null;
                copyDataPersenter.writeNum=0;
                SPUtil.getInstance(this).removeMessage(SPUtil.COPY_TIME);
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
                     handler.post(new Runnable() {
                         public void run() {
                             DialogUtils.closeProgress();
                             copyDataPersenter.showTripDialog(red3.toString());
                         }
                     });
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
                case BleContant.WIRTE_NEW_DEVICE_TIME:
                      DialogUtils.showProgress(CopyDataActivity.this, "设备读取蓝牙APP数据记录信息指令");
                      break;
                case BleContant.WIRTE_NEW_DEVICE_CODE:
                      DialogUtils.showProgress(CopyDataActivity.this, "设备读取蓝牙APP 原始设备的统一编码");
                      break;
                case BleContant.WRITE_NEW_DEVICE_LONG_DATA:
                     handler.post(new Runnable() {
                        public void run() {
                            DialogUtils.closeProgress();
                            copyDataPersenter.showCopyDialog();
                        }
                    });
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
                    DialogUtils.closeProgress();
                    break;
                //初始化通道成功
                case BleService.ACTION_ENABLE_NOTIFICATION_SUCCES:
                    //发送蓝牙命令
                    if(type==1){
                        sendData(BleContant.COPY_DEVICE_DATA);
                    }else{
                        sendData(BleContant.WRITE_NEW_DEVICE_CMD);
                    }
                    break;
                //接收到了回执的数据
                case BleService.ACTION_DATA_AVAILABLE:
                    //处理设备回执的数据
                    getDeviceData(intent.getStringExtra(BleService.ACTION_EXTRA_DATA));
                    break;
                //读取数据超时
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    DialogUtils.closeProgress();
                    if(type==1){
                        dialogView = new DialogView(dialogView,mContext, "读取数据超时", "好的", null, new View.OnClickListener() {
                            public void onClick(View v) {
                                dialogView.dismiss();
                            }
                        }, null);
                        dialogView.show();
                    }else{
                        dialogView = new DialogView(dialogView,mContext, "拷贝数据超时", "好的", null, new View.OnClickListener() {
                            public void onClick(View v) {
                                dialogView.dismiss();
                            }
                        }, null);
                        dialogView.show();
                    }
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    DialogUtils.closeProgress();
                    if(type==1){
                        dialogView = new DialogView(dialogView,mContext, "读取数据记录出现故障", "好的", null, new View.OnClickListener() {
                            public void onClick(View v) {
                                dialogView.dismiss();
                            }
                        }, null);
                        dialogView.show();
                    }else{
                        dialogView = new DialogView(dialogView,mContext, "拷贝数据出现故障", "好的", null, new View.OnClickListener() {
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
     * 处理设备回执的数据
     * @param data
     */
    //true：表示读取的命令还没完毕， false：表示读取的命令已发送完全部
    private boolean redIsSend=true;
    //true：表示可以重发上条读取的命令
    private int repeatNum=0;
    private void getDeviceData(String data){
        if(type==1){
            switch (SEND_STATUS){
                case BleContant.COPY_DEVICE_DATA:
                      red1=data;
                      //发送获取统一编码的命令
                      sendData(BleContant.COPY_DEVICE_ID);
                      break;
                case BleContant.COPY_DEVICE_ID:
                      red2=data;
                      //发送设置根据时间段读取设备里面的数据
                      redIsSend=copyDataPersenter.setRed3Cmd(red1);
                      sendData(BleContant.RED_DEVICE_DATA_BY_TIME);
                      break;
                //读取设备数据结束了
                case BleContant.RED_DEVICE_DATA_BY_TIME:
                     saveSD.append(data);
                     if(data.length()>256){
                         data=data.substring(18,data.length()-8);
                     }

                     //如果长度不够就重新发送
                     if(data.length()%256!=0){
                         if(repeatNum<2){
                             handler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     sendData(BleContant.RED_DEVICE_DATA_BY_TIME);
                                 }
                             },50);
                             repeatNum++;
                         }else{
                             //关闭读取时的进度框
                             copyDataPersenter.closeTripDialog();
                             dialogView = new DialogView(dialogView,CopyDataActivity.this, "读取到的数据长度不是128的倍数","知道了",null, new View.OnClickListener() {
                                 public void onClick(View v) {
                                     repeatNum=0;
                                     dialogView.dismiss();
                                 }
                             }, null);
                             dialogView.show();
                         }
                     }
                     //表示重读了几次都不是128的倍数
                    if(repeatNum>=2){
                        return;
                    }
                     red3.append(data);
                     if(redIsSend){
                         repeatNum=0;
                         redIsSend=copyDataPersenter.setRed3Cmd(red1);
                         sendData(BleContant.RED_DEVICE_DATA_BY_TIME);
                     }else{
                         //断开蓝牙连接
                         MainActivity.bleService.disconnect();
                         ToastUtil.showLong("蓝牙连接断开！");
                         copyDataPersenter.showRedComplete(red3.toString());
                     }
                      break;
                default:
                      break;
            }
            return;
        }

        if(type==2){
            switch (SEND_STATUS){
                case BleContant.WRITE_NEW_DEVICE_CMD:
                     if(data.endsWith(">OK")){
                         return;
                     }
                     if(data.equals("GDRECORDXXR")){
                         SendBleStr.WIRTE_NEW_DEVICE_TIME=red1;
                         sendData(BleContant.WIRTE_NEW_DEVICE_TIME);
                     }
                      break;
                case BleContant.WIRTE_NEW_DEVICE_TIME:
                      if(data.equals("GDIDR")){
                          SendBleStr.WIRTE_NEW_DEVICE_CODE=red2;
                          sendData(BleContant.WIRTE_NEW_DEVICE_CODE);
                      }
                      break;
                case BleContant.WIRTE_NEW_DEVICE_CODE:
                      if(data.startsWith("GDRECORDA")){
                          copyDataPersenter.setWriteData(red3.toString(),data);
                          sendData(BleContant.WRITE_NEW_DEVICE_LONG_DATA);
                      }
                      break;
                case BleContant.WRITE_NEW_DEVICE_LONG_DATA:
                      //拷贝完成
                      if(data.endsWith(">OK")){
                          //断开蓝牙连接
                          MainActivity.bleService.disconnect();
                          ToastUtil.showLong("蓝牙连接断开！");
                          copyDataPersenter.showCopyComplete();
                          return;
                      }
                      boolean b=copyDataPersenter.setWriteData(red3.toString(),data);
                      if(b){
                          sendData(BleContant.WRITE_NEW_DEVICE_LONG_DATA);
                      }
                      break;
                default:
                    break;
            }
        }
    }


    /**
     * 将读取的数据存储在本地
     */
    public void saveSDCard(){
        String filePath = FileUtils.createFile(red2+".txt", "\n\n\n\n"+red1+"\n\n\n\n"+red2+"\n\n\n\n"+saveSD.toString());
        dialogView = new DialogView(dialogView,CopyDataActivity.this, "读取的数据.txt文件已创建成功，目录是：" + filePath, "确定", null, new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
            }
        }, null);
        dialogView.show();
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
