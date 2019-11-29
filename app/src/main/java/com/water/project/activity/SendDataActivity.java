package com.water.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.adapter.SendDataAdapter;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.presenter.SendDataPersenter;
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
 * 发送数据
 * Created by Administrator on 2019/11/15.
 */

public class SendDataActivity extends BaseActivity {

    @BindView(R.id.tv_head)
    TextView tvHead;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    //MVP对象
    private SendDataPersenter sendDataPersenter;
    /**
     * true：正在接受数据，不能离开界面
     * false：反之
     */
    private boolean isSend = false;
    //下发命令的编号
    private int SEND_STATUS;
    private DialogView dialogView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_data);
        ButterKnife.bind(this);
        initView();
        register();//注册广播
    }

    /**
     * 初始化
     */
    private void initView() {
        //注册EventBus
        EventBus.getDefault().register(this);
        //实例化MVP
        sendDataPersenter = new SendDataPersenter(this);
        tvHead.setText("发送数据");
    }


    @OnClick({R.id.lin_back, R.id.tv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                sendDataPersenter.gotoBack(isSend);
                break;
            //发送命令
            case R.id.tv_send:
                dialogView = new DialogView(this, "确定发送数据吗！", "确定", "取消", new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    public void onClick(View v) {
                        dialogView.dismiss();
                        tvStatus.setText(null);
                        sendData(BleContant.MENU_SEND_DATA);
                    }
                }, new View.OnClickListener() {
                    public void onClick(View v) {
                        SendDataActivity.this.finish();
                    }
                });
                dialogView.show();
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
        if (!BleUtils.isEnabled(SendDataActivity.this, MainActivity.mBtAdapter)) {
            return;
        }
        //如果蓝牙连接断开，就扫描重连
        if (MainActivity.bleService.connectionState == MainActivity.bleService.STATE_DISCONNECTED) {
            //扫描并重连蓝牙
            final Ble ble = (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE, Ble.class);
            if (null != ble) {
                DialogUtils.showProgress(SendDataActivity.this, "扫描并连接蓝牙设备...");
                MainActivity.bleService.scanDevice(ble.getBleName());
            }
            return;
        }
        DialogUtils.showProgress(SendDataActivity.this, "正在发送数据...");
        SendBleStr.sendBleData(status);
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
                    isSend = false;
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
                    final String data = intent.getStringExtra(BleService.ACTION_EXTRA_DATA);
                    showData(data);
                    break;
                case BleService.ACTION_INTERACTION_TIMEOUT:
                    isSend = false;
                    sendDataPersenter.timeOut(SEND_STATUS);
                    break;
                case BleService.ACTION_SEND_DATA_FAIL:
                    isSend = false;
                    sendDataPersenter.sendCmdFail(SEND_STATUS);
                    break;
                case BleService.ACTION_GET_DATA_ERROR:
                    isSend = false;
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
                sendData(cmd);
                break;
            default:
                break;
        }
    }


    /**
     * 展示设备回执的数据
     *
     * @param data
     */
    private void showData(String data) {
        if (data.startsWith("GDBLEGPRSSENDDATA-11.") || data.startsWith("GDBLEGPRSSENDDATA-12.")) {
            isSend = false;
            SEND_STATUS = BleContant.NOT_SEND_DATA;
        } else {
            isSend = true;
        }
        /**
         * 如果是旧设备，就直接显示回执的数据
         */
        if (BleUtils.getVersion(this) == 1) {
            tvStatus.setText(data);
            return;
        }
        /**
         * 新设备做判断来显示对应的文案
         */
        if (data.startsWith("GDBLEGPRSSENDDATA-01.")) {
            tvStatus.setText("正在搜索无线信号");
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-02.")) {
            tvStatus.setText("正在搜索无线信号");
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-03.")) {
            tvStatus.setText("未安装SIM卡或SIM卡安装错误,请检查!");
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-04.")) {
            tvStatus.setText("SIM卡正常,但无法找到无线信号. 请用手机或其它设备测试信号");
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-05.")) {
            tvStatus.setText("已成功找到无线信号信号质量： \n" + getPercentage(data));
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-06.")) {
            tvStatus.setText("正在登录 internet网络信号质量： \n" + getPercentage(data));
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-07.")) {
            tvStatus.setText("无法登录 internet 网络");
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-08.")) {
            tvStatus.setText("正在连接数据服务器信号质量： \n" + getPercentage(data));
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-09.")) {
            tvStatus.setText("连接数据服务器失败");
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-10.")) {
            tvStatus.setText("正在发送数据");
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-11.")) {
            tvStatus.setText("发送数据成功");
            return;
        }
        if (data.startsWith("GDBLEGPRSSENDDATA-12.")) {
            tvStatus.setText("发送数据失败");
            return;
        }
    }


    /**
     * 获取百分比数据
     *
     * @param data
     */
    private String getPercentage(String data) {
        String[] strs = data.split("\\.");
        if (strs == null && strs.length == 1) {
            return null;
        }
        final int num = Integer.parseInt(strs[1]);
        if (num >= 0 && num <= 20) {
            return strs[1] + "%(很弱)";
        }
        if (num >= 21 && num <= 31) {
            return strs[1] + "%(较弱)";
        }
        if (num >= 32 && num <= 45) {
            return strs[1] + "%(偏弱)";
        }
        if (num >= 46 && num <= 58) {
            return strs[1] + "%(一般)";
        }
        if (num >= 59 && num <= 69) {
            return strs[1] + "%(良好)";
        }
        return strs[1] + "%(较好)";
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            sendDataPersenter.gotoBack(isSend);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
    }
}
