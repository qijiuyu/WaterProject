package com.water.project.utils.ble;

import com.water.project.utils.BuglyUtils;
import com.water.project.utils.LogUtils;
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

    //设置三个ip
    public static String SET_IP_PORT;

    //校测前先读取水位偏移量
    public static String SEND_CHECK_ERROR="GD&#PYR001T";

    //设置水位埋深误差数据
    public static String SET_DATA_CHECK;

    //校测前先读取水温偏移量
    public static String RED_SHUI_WEN_PYL="GD&#TYR001T";

    //设置水温误差数据
    public static String SEND_DATA_SHUI_WEN;

    //校测前先读取电导率偏移量
    public static  String RED_DIAN_DAO_LV_PYL="GD&#CYR001T";

    //设置水温电导率数据
    public static  String SEND_DATA_DIAN_DAO_LV;

    //设置统一编码，SIM卡号
    public static void sendSetCodeSim(String code,String sim,String data){
        StringBuffer stringBuffer=new StringBuffer();
        String[] strings=data.split(";");
        strings[1]=code;
        strings[6]=sim;
        for (int i=0;i<strings.length;i++){
            stringBuffer.append(strings[i]+";");
        }
        SET_CODE_PHONE=stringBuffer.toString();
    }


    /**
     * 设置探头埋深
     * @param data
     */
    public static void sendSetTanTou(String data){
        StringBuffer stringBuffer=new StringBuffer("GDLINEW");
        int position=data.indexOf(".");
        if(position==-1) {
            for (int i=0;i<4-data.length();i++){
                stringBuffer.append("0");
            }
            stringBuffer.append(data+".000");
        }else{
            final int hou=data.length() - position - 1;
            final int qian=data.length()-hou-1;
            for (int i=0;i<4-qian;i++){
                stringBuffer.append("0");
            }
            stringBuffer.append(data);
            for (int i=0;i<3-hou;i++){
                stringBuffer.append("0");
            }
        }
        SET_TANTOU=stringBuffer.toString();
    }


    /**
     * 设置采集频率
     * @param startTime
     * @param hour
     */
    public static void sendCaiJi(String startTime,String hour){
        StringBuffer stringBuffer=new StringBuffer("GDREADW");
        stringBuffer.append(startTime+",");
        int minuts=((Integer.parseInt(hour)) * 60);
        for(int i=0;i<4-String.valueOf(minuts).length();i++){
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
        stringBuffer.append(startTime.replaceAll(" ","").substring(10,12)+",");
        if(hour.length()==1){
            stringBuffer.append("0");
        }
        stringBuffer.append(hour);
        SET_FA_SONG_PIN_LU=stringBuffer.toString();
    }


    /**
     * 设置水位埋深误差数据
     * @param data:偏移量
     */
    public static void setMS_check(String wucha,String data){
        StringBuffer stringBuffer=new StringBuffer("GD&>#PYW001T");
        data=data.replace("GD&>#PYR001T","");

        final double pyl=Double.parseDouble(data);
        final double wc=Double.parseDouble(wucha);

        String result=Util.sum(pyl,wc)+"";

        if(result.contains("-")){
            stringBuffer.append("-");
        }else{
            stringBuffer.append("+");
        }
        result=result.replace("-","");
        final int index=result.indexOf(".");
        //判断小数点前面几位
        for(int i=0;i<4-index;i++){
            stringBuffer.append("0");
        }
        stringBuffer.append(result);

        //判断小数点后面几位
        for(int i=0;i<4-(result.length()-index-1);i++){
            stringBuffer.append("0");
        }
        SET_DATA_CHECK=stringBuffer.toString();
    }


    /**
     * 校测水温
     * @param wucha
     * @param data
     */
    public static void setSW_check(String wucha,String data){
        StringBuffer stringBuffer=new StringBuffer("GD&#TYW001T");
        data=data.replace("GD&>#TYR001T","");
        final double pyl=Double.parseDouble(data);
        final double wc=Double.parseDouble(wucha);
        String result=Util.sum(pyl,wc)+"";

        if(result.contains("-")){
            stringBuffer.append("-");
        }else{
            stringBuffer.append("+");
        }
        result=result.replace("-","");
        final int index=result.indexOf(".");
        //判断小数点前面几位
        for(int i=0;i<2-index;i++){
            stringBuffer.append("0");
        }
        stringBuffer.append(result);

        //判断小数点后面几位
        for(int i=0;i<2-(result.length()-index-1);i++){
            stringBuffer.append("0");
        }
        SEND_DATA_SHUI_WEN=stringBuffer.toString();
        LogUtils.e("_____"+SEND_DATA_SHUI_WEN);
    }


    /**
     * 校测电导率
     * @param wucha
     * @param data
     */
    public static void setDDL_check(String wucha,String data){
        StringBuffer stringBuffer=new StringBuffer("GD&#CYW001T");
        data=data.replace("GD&>#CYR001T","");
        final double pyl=Double.parseDouble(data);
        final double wc=Double.parseDouble(wucha);
        String result=Util.sum(pyl,wc)+"";

        if(result.contains("-")){
            stringBuffer.append("-");
        }else{
            stringBuffer.append("+");
        }
        result=result.replace("-","");
        final int index=result.indexOf(".");
        //判断小数点前面几位
        for(int i=0;i<6-index;i++){
            stringBuffer.append("0");
        }
        stringBuffer.append(result);

        //判断小数点后面几位
        for(int i=0;i<2-(result.length()-index-1);i++){
            stringBuffer.append("0");
        }
        SEND_DATA_DIAN_DAO_LV=stringBuffer.toString();
        LogUtils.e("_________"+SEND_DATA_DIAN_DAO_LV);
    }


    /**
     * 设置三个ip
     * @param address1
     * @param address2
     * @param address3
     * @param ip1
     * @param ip2
     * @param ip3
     * @param port1
     * @param port2
     * @param port3
     * @param apn
     */
    public static void setIpPort(String data,String address1, String address2,String address3,String ip1,String ip2,String ip3,String port1,String port2,String port3,String apn){
        String[] strings=data.split(";");
        StringBuffer stringBuffer=new StringBuffer();
        strings[0]=("GDS"+address1+address2+address3);

        strings[2]=(ip1+","+port1);
        strings[3]=(ip2+","+port2);
        strings[4]=(ip3+","+port3);

        strings[5]=apn;

        for (int i=0;i<strings.length;i++){
            stringBuffer.append(strings[i]+";");
        }
        SET_IP_PORT=stringBuffer.toString();
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
            //设置采集频率
            case BleContant.SET_CAI_JI_PIN_LU:
                 SendBleDataManager.getInstance().sendData(SET_CAI_JI_PIN_LU,type);
                 break;
             //设置发送频率
            case BleContant.SET_FA_SONG:
                 SendBleDataManager.getInstance().sendData(SET_FA_SONG_PIN_LU,type);
                 break;
            //设置误差数据
            case BleContant.SET_DATA_CHECK:
                 SendBleDataManager.getInstance().sendData(SET_DATA_CHECK,type);
                 break;
            //查询实时数据
            case BleContant.SEND_REAL_TIME_DATA:
                 SendBleDataManager.getInstance().sendData(SEND_REAL_TIME_DATA,type);
                 break;
            //设置网络数据
            case BleContant.SET_IP_PORT:
                 SendBleDataManager.getInstance().sendData(SET_IP_PORT,type);
                 break;
            //校测前先读取水位偏移量
            case BleContant.SEND_CHECK_ERROR:
                 SendBleDataManager.getInstance().sendData(SEND_CHECK_ERROR,type);
                 break;
            //校测前先读取水温偏移量
            case BleContant.RED_SHUI_WEN_PYL:
                  SendBleDataManager.getInstance().sendData(RED_SHUI_WEN_PYL,type);
                  break;
            //设置水温误差数据
            case BleContant.SEND_DATA_SHUI_WEN:
//                BuglyUtils.uploadBleMsg("发送给设备的水温校正数据是："+SEND_DATA_SHUI_WEN);
                  SendBleDataManager.getInstance().sendData(SEND_DATA_SHUI_WEN,type);
                  break;
            //校测前先读取电导率偏移量
            case BleContant.RED_DIAN_DAO_LV_PYL:
                  SendBleDataManager.getInstance().sendData(RED_DIAN_DAO_LV_PYL,type);
                  break;
            //设置水温电导率数据
            case BleContant.SEND_DATA_DIAN_DAO_LV:
//                BuglyUtils.uploadBleMsg("发送给设备的电导率数据是："+SEND_DATA_DIAN_DAO_LV);
                 SendBleDataManager.getInstance().sendData(SEND_DATA_DIAN_DAO_LV,type);
                 break;
             default:
                 break;

        }
    }
}
