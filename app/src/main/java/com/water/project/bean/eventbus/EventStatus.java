package com.water.project.bean.eventbus;

public class EventStatus {
    /**
     * 蓝牙扫描成功
     */
    public final static int ACTION_SCAN_SUCCESS = 111;

    /**
     * 蓝牙断开连接
     */
    public final static int ACTION_GATT_DISCONNECTED = 222;
    /**
     * 蓝牙接收到了数据
     */
    public final static int ACTION_DATA_AVAILABLE =333;
    /**
     * 接收到了设备回执的数据
     */
    public final static int ACTION_EXTRA_DATA =444;
    /**
     * 蓝牙通道建立成功
     */
    public final static int ACTION_ENABLE_NOTIFICATION_SUCCES = 555;
    /**
     * 没有发现指定蓝牙
     */
    public final static int ACTION_NO_DISCOVERY_BLE = 666;

    /**
     * 30秒扫描完毕
     */
    public final static int ACTION_SCAM_DEVICE_END = 777;

    /**
     * 蓝牙数据交互超时
     */
    public final static int ACTION_INTERACTION_TIMEOUT =888;

    /**
     * 发送数据失败
     */
    public final static int ACTION_SEND_DATA_FAIL = 999;

    /**
     * 回执的数据有error
     */
    public final static int ACTION_GET_DATA_ERROR=1000;


    public static final int SHOW_DEVICE_DATA=9999;

    //新版参数设置中采集时间间隔
    public static final int NEW_SETTING_CJSJJG=10000;

    //新版参数设置中发送时间间隔
    public static final int NEW_SETTING_FSSJJG=10001;

    //点击去连接蓝牙
    public static final int CONNCATION_BLE=10002;

    //重新扫描蓝牙设备
    public static final int RESUME_SCAN_BLE=10003;

    //发送命令
    public static final int SEND_CHECK_MCD=10004;

    //选择的gprs模式
    public static final int SELECT_GRPS=10005;

    //选择的补发次数
    public static final int SELECT_SEND_NUM=10006;

}
