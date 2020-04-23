package com.water.project.activity.menu6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.activity.MainActivity;
import com.water.project.adapter.BAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.presenter.SendDataPersenter;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.ble.BleContant;
import com.water.project.utils.ble.SendBleStr;
import com.water.project.view.DialogView;
import com.water.project.view.MeasureListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发送数据
 * Created by Administrator on 2019/11/15.
 */

public class BActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.listView)
    MeasureListView listView;
    @BindView(R.id.tv_dian_ya)
    TextView tvDianYa;
    @BindView(R.id.tv_list)
    TextView tvList;
    //MVP对象
    private SendDataPersenter sendDataPersenter;
    //下发命令的编号
    private int SEND_STATUS;
    //计数器
    private Timer mTimer;
    private int time = 0;
    private Handler handler=new Handler();
    private DialogView dialogView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_b);
        ButterKnife.bind(this);
        initView();
        //注册广播
        register();
    }

    /**
     * 初始化
     */
    private void initView() {
        //注册EventBus
        EventBus.getDefault().register(this);
        //实例化MVP
        sendDataPersenter = new SendDataPersenter(this);
        tvHead.setText("北斗通讯发送实时数据");
    }


    @OnClick({R.id.lin_back, R.id.tv_send, R.id.tv_send2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                finish();
                break;
            //让设备通过北斗方式发送实时数据
            case R.id.tv_send:
                if(time>0){
                    ToastUtil.showLong("请等待60秒后再进行操作");
                    return;
                }
                sendData(BleContant.BEI_DOU_FANG_SHI_SEND_DATA);
                break;
            default:
                break;
        }
    }


    /**
     * 发送蓝牙命令
     */
    public void sendData(int status) {
        SEND_STATUS = status;
        //判断蓝牙是否打开
        if (!BleUtils.isEnabled(BActivity.this, MainActivity.mBtAdapter)) {
            return;
        }
        //如果蓝牙连接断开，就扫描重连
        if (MainActivity.bleService.connectionState == MainActivity.bleService.STATE_DISCONNECTED) {
            //扫描并重连蓝牙
            final Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
            if (null != ble) {
                DialogUtils.showProgress(BActivity.this, "扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        if(SEND_STATUS==BleContant.BEI_DOU_FANG_SHI_SEND_DATA){
            DialogUtils.showProgress(BActivity.this, "正在发送数据,请稍候...");
        }else{
            DialogUtils.showProgress(BActivity.this, "正在读取信号强度,请稍候...");
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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //扫描不到指定蓝牙设备
                case BleService.ACTION_NO_DISCOVERY_BLE:
                    sendDataPersenter.resumeScan(SEND_STATUS);
                    break;
                //蓝牙断开连接
                case BleService.ACTION_GATT_DISCONNECTED:
                    sendDataPersenter.bleDisConnect();
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

                    //延时60秒后
                    if(SEND_STATUS==BleContant.BEI_DOU_FANG_SHI_SEND_DATA){
                        startTime();
                    }

                    if(data.endsWith(">ERR")){
                        dialogView = new DialogView(dialogView,BActivity.this, "北斗通讯部分出现故障，请联系维护人员!","好的", null, null, null);
                        dialogView.show();
                    }else{
                        data=data.replace("GDBDSQ","").replace(">OK", "");
                        //显示信号列表
                        tvList.setVisibility(View.VISIBLE);
                        String[] strs=data.split(",");
                        BAdapter bAdapter=new BAdapter(BActivity.this,strs);
                        listView.setAdapter(bAdapter);
                        //显示电压值

                        String strDy=strs[strs.length-1];
                        if(strDy.indexOf("V")!=-1){
                            String[] dianya=strDy.split("V");
                            tvDianYa.setText("发送数据成功，北斗通讯部分电压值："+dianya[1]+"V\n请联系接收中心查看数据");
                        }
                    }
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    if(SEND_STATUS==BleContant.BEI_DOU_FANG_SHI_SEND_DATA){
                        //动态改变秒数
                        startTime();
                    }
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


    /**
     * EventBus注解
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe
    public void onEvent(EventType eventType) {
        switch (eventType.getStatus()) {
            //发送命令
            case EventStatus.SEND_CHECK_MCD:
                final int cmd = (int) eventType.getObject();
                if(SEND_STATUS==BleContant.BEI_DOU_FANG_SHI_SEND_DATA && time>0){
                    ToastUtil.showLong("请等待60秒后再进行操作");
                    return;
                }
                sendData(cmd);
                break;
            default:
                break;
        }
    }


    /**
     * 动态改变验证码秒数
     */
    private void startTime() {
        time=60;
        //保存计时时间
        SPUtil.getInstance(this).addString("new_time", String.valueOf((System.currentTimeMillis() + 60000)));
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (time <= 0) {
                    handler.post(new Runnable() {
                        public void run() {
                            mTimer.cancel();
                            tvSend.setText("让设备通过北斗方式发送实时数据");
                            SPUtil.getInstance(BActivity.this).removeMessage("new_time");
                            tvSend.setBackgroundColor(getResources().getColor(R.color.color_1fc37f));
                        }
                    });
                } else {
                    --time;
                    handler.post(new Runnable() {
                        public void run() {
                            tvSend.setText("让设备通过北斗方式发送实时数据("+time+")");
                            tvSend.setBackgroundColor(getResources().getColor(R.color.color_DADADA));
                        }
                    });
                }
            }
        }, 0, 1000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
        if(mTimer!=null){
            mTimer.purge();
            mTimer=null;
        }
    }

}
