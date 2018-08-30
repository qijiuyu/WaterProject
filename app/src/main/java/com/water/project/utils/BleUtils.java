package com.water.project.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import java.lang.ref.SoftReference;

/**
 * 蓝牙工具类
 * Created by lyn on 2017/3/1.
 */

public class BleUtils {

    /***
     * 判断蓝牙是否开启
     *
     * @return true 已开启 false 关闭
     */
    public static boolean isEnabled(Activity activity, BluetoothAdapter bluetoothAdapter) {
        //软饮用，防止内存泄漏
        SoftReference<Activity> activitySoftReference = new SoftReference<>(activity);
        // 确保蓝牙在设备上可以开启
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activitySoftReference.get().startActivity(enableBtIntent);
            return false;
        }
        return true;
    }


}
