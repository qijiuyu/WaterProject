package com.water.project.utils.ble;

/**
 * Created by Administrator on 2018/9/1.
 */

public class BleContant {
    //没有要发送的数据
    public  static final int NOT_SEND_DATA=0001;

    //读取设备版本号
    public static final int RED_DEVICE_VERSION=0x009;

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

    //读取设备记录
    public static final int RED_DEVICE_RECOFD=1016;

    //新设备读取统一编码
    public static final int RED_NEW_GET_CODE=1017;

    //发送数据菜单：发送数据
    public static final int MENU_SEND_DATA=1018;

    //设置设备时间
    public static final int SEND_DEVICE_TIME=1019;

    //读取设备时间
    public static final int RED_DEVICE_TIME=1020;

    //读取原始设备数据记录信息指令
    public static final int COPY_DEVICE_DATA=1021;

    //读取设备的统一编码
    public static final int COPY_DEVICE_ID=1022;

    //发送根据时间段读取设备里面的数据
    public static final int RED_DEVICE_DATA_BY_TIME=1023;

    //蓝牙APP启动拷贝数据记录命令
    public static final int WRITE_NEW_DEVICE_CMD=1024;

    //给新设备写入时间数据
    public static final int WIRTE_NEW_DEVICE_TIME=1025;

    //给新设备写入统一编码数据
    public static final int WIRTE_NEW_DEVICE_CODE=1026;

    //给新设备写入大量数据
    public static final int  WRITE_NEW_DEVICE_LONG_DATA=1027;

    //设置北斗的中心号码
    public static final int SET_CENTER_MOBILE=1028;

    //获取设备型号
    public static final int GET_DEVICE_MODEL=1029;

    //让设备通过北斗方式发送实时数据
    public static final int BEI_DOU_FANG_SHI_SEND_DATA=1030;

    //读取设备北斗信号强度
    public static final int RED_BEI_DOU_XIN_HAO_QIANG_DU=1031;

    //读取本地的txt文档数据，下发给设备
    public static final int SEND_TXT_CONTENT=1032;

    //发送根据时间段读取设备里面的数据
    public static final int RED_DEVICE_DATA_BY_TIME2=1033;

    //读取采集路数
    public static final int RED_CAIJI_ROAD=1034;

    //根据路数读取实时数据
    public static final int RED_TIME_DATA_BY_ROAD=1035;

    //读取多路参数设置里面的统一编码
    public static final int RED_MORE_SETTING_CODE=1036;

    //读取多路参数设置里面的探头埋深
    public static final int RED_MORE_SETTING_TANTOU=1037;

    //设置采集路数
    public static final int SET_CAIJI_ROAD=1038;

    //设置多路参数的统一编码
    public static final int SET_MORE_SETTING_CODE=1039;

    //设置多路参数的探头埋深
    public static final int SET_MORE_SETTING_TANTOU=1040;
}
