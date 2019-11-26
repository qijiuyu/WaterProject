package com.water.project.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

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


    /**
     * 获取设备版本
     *
     * @return
     */
//    public static int getVersion(Context context) {
//        String version = SPUtil.getInstance(context).getString(SPUtil.DEVICE_VERSION);
//        if (TextUtils.isEmpty(version)) {
//            return 1;
//        }
//        if(version.contains("V3.00")){
//            return 3;
//        }
//        if (version.startsWith("GDBBRGDsender") || version.startsWith("GDBBRZKGD2000")) {
//            return 1;
//        }
//        if (version.startsWith("GDBBRV")) {
//            return 2;
//        }
//        return 1;
//    }


    /**
     * 获取设备版本
     *
     * @return
     */
    public static int getVersion(Context context) {
        String version = SPUtil.getInstance(context).getString(SPUtil.DEVICE_VERSION);
        if (TextUtils.isEmpty(version)) {
            return 1;
        }
        if(version.contains("V3.00")){
            return 3;
        }
        String[] vs = version.split("-");
        if (null == vs || vs.length == 0) {
            return 1;
        }
        if (version.startsWith("GDBBRGDsender")) {
            return 1;
        }
        if (version.startsWith("GDBBRZKGD2000")) {
            if (vs.length == 2) {
                return 1;
            }
            if (vs[2].contains("V1") || vs[2].contains("V2")) {
                return 1;
            }
            return 2;
        }
        return 1;
    }

}
