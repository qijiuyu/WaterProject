package com.water.project.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

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
    public static int getVersion(Context context) {
        String version = SPUtil.getInstance(context).getString(SPUtil.DEVICE_VERSION);
        if (TextUtils.isEmpty(version)) {
            return 1;
        }
        if(version.contains("V3.00")){
            return 3;
        }
//        if (version.startsWith("GDBBRGDsender") || version.startsWith("GDBBRZKGD2000")) {
//            return 1;
//        }
        if (version.startsWith("GDBBRV")) {
            return 2;
        }else{
            return 1;
        }
    }


    /**
     * 将字符串进行20字节的截取
     * @param inputString
     * @param length
     * @return
     */
    public static List<String> getSendData(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }


    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString
     *            原始字符串
     * @param length
     *            指定长度
     * @param size
     *            指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length,int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length, (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }


    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str
     *            原始字符串
     * @param f
     *            开始位置
     * @param t
     *            结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }
}
