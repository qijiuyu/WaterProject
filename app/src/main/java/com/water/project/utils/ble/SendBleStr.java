package com.water.project.utils.ble;

import com.water.project.utils.Util;

/**
 * Created by Administrator on 2018/9/1.
 */

public class SendBleStr {

    //读取统一编码，SIM卡号
    public static final String GET_CODE_PHONE="GDGET";

    //读取探头埋深
    public static final String GET_TANTOU="GDLINER";

    //读取采集频率
    public static final String GET_CAI_JI_PIN_LU="GDREADR";

    //读取发送频率
    public static final String GET_FA_SONG_PIN_LU="GDSENDR";

    //统一编码，SIM卡号
    public static  String SET_CODE_PHONE;

    //设置探头埋深
    public static String SET_TANTOU;

    //设置采集频路
    public  static String SET_CAI_JI_PIN_LU;

    //设置发送频率
    public static String SET_FA_SONG_PIN_LU;


    //查询实时数据
    public static String SEND_REAL_TIME_DATA="GDCURRENT";

    //设置误差数据
    public static String SET_DATA_CHECK;

    //设置统一编码，SIM卡号
    public static void sendSetCodeSim(String code,String sim,String data){
        StringBuffer stringBuffer=new StringBuffer();
        String[] strings=data.split(";");
        strings[1]=code;
        strings[6]=sim;
        for (int i=0;i<strings.length;i++){
            stringBuffer.append(strings[i]);
        }
        SET_CODE_PHONE=stringBuffer.toString();
    }


    /**
     * 设置探头埋深
     * @param data
     */
    public static void sendSetTanTou(String data){
        StringBuffer stringBuffer=new StringBuffer("GDLINEW");
        final int length=8-(data.length());
        for (int i=0;i<length;i++){
            stringBuffer.append("0");
        }
        stringBuffer.append(data);
        SET_TANTOU=stringBuffer.toString();
    }


    /**
     * 设置采集频路
     * @param startTime
     * @param hour
     */
    public static void sendCaiJi(String startTime,String hour){
        StringBuffer stringBuffer=new StringBuffer("GDREADW");
        stringBuffer.append(startTime+",");
        int minuts=((Integer.parseInt(hour)) * 60);
        if(minuts<1000){
            stringBuffer.append("0");
        }
        stringBuffer.append(minuts);
        SET_CAI_JI_PIN_LU=stringBuffer.toString();
    }


    /**
     * 设置发送频率
     * @param startTime
     * @param hour
     */
    public static void setFaSong(String startTime,String hour){
        StringBuffer stringBuffer=new StringBuffer("GDSENDW");
        stringBuffer.append(startTime+",");
        stringBuffer.append(hour);
        SET_FA_SONG_PIN_LU=stringBuffer.toString();
    }


    /**
     * 设置误差数据
     * @param data
     */
    public static void setCheck(String data){
        StringBuffer stringBuffer=new StringBuffer("GD&>#PYW001T");
        final double CMdata=Integer.parseInt(data.replace("-",""));
        if(data.contains("-")){
            stringBuffer.append("-");
        }else{
            stringBuffer.append("+");
        }
        data= Util.setDouble(CMdata,4);
        final int length=9-(data.length());
        for (int i=0;i<length;i++){
             stringBuffer.append("0");
        }
        SET_DATA_CHECK=stringBuffer.toString();
    }

    public static void sendBleData(int status,int type){
        switch (status){
            //读取统一编码，SIM卡号
            case BleContant.SEND_GET_CODE_PHONE:
                 SendBleDataManager.getInstance().sendData(GET_CODE_PHONE,type);
                 break;
            //读取探头埋深
            case BleContant.SEND_GET_TANTOU:
                 SendBleDataManager.getInstance().sendData(GET_TANTOU,type);
                 break;
            //读取采集频率
            case BleContant.SEND_CAI_JI_PIN_LU:
                 SendBleDataManager.getInstance().sendData(GET_CAI_JI_PIN_LU,type);
                 break;
            //读取发送频率
            case BleContant.SEND_FA_SONG_PIN_LU:
                 SendBleDataManager.getInstance().sendData(GET_FA_SONG_PIN_LU,type);
                 break;
            //设置统一编码，SIM卡号
            case BleContant.SET_CODE_PHONE:
                 SendBleDataManager.getInstance().sendData(SET_CODE_PHONE,type);
                 break;
            //设置探头埋深
            case BleContant.SET_TANTOU:
                 SendBleDataManager.getInstance().sendData(SET_TANTOU,type);
                 break;
            //查询实时数据
            case BleContant.SEND_REAL_TIME_DATA:
                 SendBleDataManager.getInstance().sendData(SEND_REAL_TIME_DATA,type);
                 break;

        }
    }
}
