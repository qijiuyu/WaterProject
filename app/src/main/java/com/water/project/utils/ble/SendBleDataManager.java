package com.water.project.utils.ble;
import android.content.Intent;
import com.water.project.service.BleService;
import com.water.project.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lyn on 2017/4/28.
 */

public class SendBleDataManager {

    private static SendBleDataManager sendBleData = new SendBleDataManager();
    private ExecutorService fixedThreadPool_ble = Executors.newSingleThreadExecutor();
    private BleService mService;

    private SendBleDataManager() {
    }


    public static SendBleDataManager getInstance() {
        return sendBleData;
    }


    public void init(BleService mService) {
        this.mService = mService;
    }

    /**
     *
     * @param data  发送的蓝牙命令
     */
    public void sendData(final String data,final int type) {
        if (mService == null || mService.getConnectionState() == BleService.STATE_DISCONNECTED) {
            return;
        }
        try {
            new Thread().sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fixedThreadPool_ble.execute(new Runnable() {
            public void run() {
                //将字符串进行20字节的截取
                final List<String> sendList=getSendData(data,20);
                //下发蓝牙命令
                boolean b = mService.writeRXCharacteristic(sendList,type);
                if (!b) {
                    b = mService.writeRXCharacteristic(sendList,type);
                    if (!b) {
                        LogUtils.e("发送数据第二次失败");
                        mService.stopTimeOut();
                        Intent intent = new Intent(mService.ACTION_SEND_DATA_FAIL);
                        mService.sendBroadcast(intent);
                    }
                }
            }
        });
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
