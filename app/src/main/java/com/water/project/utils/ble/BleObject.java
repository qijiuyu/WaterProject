package com.water.project.utils.ble;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import com.water.project.service.BleService;
import com.water.project.utils.BleUtils;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.LogUtils;

public class BleObject {

    private Activity activity;
    private static BleObject bleObject;
    public BleService bleService;

    public static BleObject getInstance(){
        if(bleObject==null){
            bleObject=new BleObject();
        }
        return bleObject;
    }


    public BleService getBleService(Activity activity){
        this.activity=activity;
        if(bleService==null){
            DialogUtils.closeProgress();
            Intent bindIntent = new Intent(activity, BleService.class);
            activity.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
        return bleService;
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            bleService = ((BleService.LocalBinder) rawBinder).getService();
            //判断蓝牙是否打开
            BleUtils.isEnabled(activity, bleService.createBluetoothAdapter());
        }

        public void onServiceDisconnected(ComponentName classname) {
        }
    };


    public void disconnect(){
        activity.unbindService(mServiceConnection);
        if(bleService!=null){
            bleService.disconnect();
        }
    }

}
