package com.water.project.presenter.new_device;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.water.project.activity.MainActivity;
import com.water.project.application.MyApplication;
import com.water.project.bean.Ble;
import com.water.project.bean.eventbus.EventStatus;
import com.water.project.bean.eventbus.EventType;
import com.water.project.service.BleService;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.view.DialogView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2019/9/23.
 */

public class New_SettingPresenter {

    public Activity activity;
    private DialogView dialogView;

    public New_SettingPresenter(Activity activity){
        this.activity=activity;
    }


    /**
     * 重新扫描蓝牙
     */
    public void resumeScan(){
        DialogUtils.closeProgress();
        dialogView = new DialogView(activity, "扫描不到该蓝牙设备，请靠近设备再进行扫描！", "重新扫描","取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                EventBus.getDefault().post(new EventType(EventStatus.RESUME_SCAN_BLE));
            }
        }, null);
        dialogView.show();
    }


    /**
     * 蓝牙连接断开
     */
    public void bleConncation(final BleService bleService){
        DialogUtils.closeProgress();
        dialogView = new DialogView(activity, "蓝牙连接断开，请靠近设备进行连接!","重新连接", "取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                DialogUtils.showProgress(activity,"蓝牙连接中...");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Ble ble= (Ble) MyApplication.spUtil.getObject(SPUtil.BLE_DEVICE,Ble.class);
                        bleService.connect(ble.getBleMac());
                    }
                },100);
            }
        }, null);
        dialogView.show();
    }


    /**
     * 读取数据超时
     */
    public void timeOut(){
        DialogUtils.closeProgress();
        dialogView = new DialogView(activity, "接收数据超时！", "重试","取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                EventBus.getDefault().post(new EventType(EventStatus.RESUME_SCAN_BLE));
            }
        }, null);
        dialogView.show();
    }


    /**
     * 下发命令失败
     */
    public void sendCmdFail(){
        DialogUtils.closeProgress();
        dialogView = new DialogView(activity, "下发命令失败！", "重试","取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                EventBus.getDefault().post(new EventType(EventStatus.RESUME_SCAN_BLE));
            }
        }, null);
        dialogView.show();
    }


    /**
     * 弹框提示
     * @param msg
     */
    public void trip(String msg){
        dialogView = new DialogView(activity, msg, "确定",null, new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
            }
        }, null);
        dialogView.show();
    }
}
