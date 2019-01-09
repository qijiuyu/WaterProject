package com.water.project.utils.ble;

/**
 * Created by Administrator on 2018/9/1.
 */

public class BleContant {
    //没有要发送的数据
    public  static final int NOT_SEND_DATA=0001;

    //读取统一编码，SIM卡号
    public static final int SEND_GET_CODE_PHONE=1000;

    //读取探头埋深
    public static final int SEND_GET_TANTOU=1001;

    //读取采集频率
    public static final int SEND_CAI_JI_PIN_LU=1002;

    //读取发送频率
    public static final int SEND_FA_SONG_PIN_LU=1003;

    //设置统一编码，SIM卡号
    public static final int SET_CODE_PHONE=1004;

    //设置探头埋深
    public static final int SET_TANTOU=1005;

    //设置采集频率
    public static final int SET_CAI_JI_PIN_LU=1006;

    //设置发送频率
    public static final int SET_FA_SONG=1007;

    //查询实时数据
    public static final int SEND_REAL_TIME_DATA=1008;

    //设置误差数据
    public static final int SET_DATA_CHECK=1009;

    //设置三个ip
    public static final int SET_IP_PORT=1010;

    //校测前先读取偏移量
    public static final int SEND_CHECK_ERROR=1011;

    //校测前先读取水温偏移量
    public static final int RED_SHUI_WEN_PYL=1012;

    //设置水温误差数据
    public static final int SEND_DATA_SHUI_WEN=1013;

    //校测前先读取电导率偏移量
    public static final int RED_DIAN_DAO_LV_PYL=1014;

    //设置水温电导率数据
    public static final int SEND_DATA_DIAN_DAO_LV=1015;
}
