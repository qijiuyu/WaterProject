package com.water.project.bean.eventbus;

public class EventStatus {

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
