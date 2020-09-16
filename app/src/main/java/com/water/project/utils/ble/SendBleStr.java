package com.water.project.utils.ble;

import android.app.Activity;
import android.text.TextUtils;

import com.water.project.bean.MoreCode;
import com.water.project.bean.MoreTanTou;
import com.water.project.utils.BuglyUtils;
import com.water.project.utils.LogUtils;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.Util;

import java.util.Map;

/**
 * Created by Administrator on 2018/9/1.
 */

public class SendBleStr {

    //读取设备的版本号
    public static final String RED_VERSION="GDBBR";

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

    //读取设备记录
    public static String RED_DEVOCE_RECORD="GDERRREAD";

    //新设备读取统一编码
    public static final String RED_NEW_GET_CODE="GDIDR";

    //发送数据菜单：发送数据
    public static final String MENU_SEND_DATA="GDBLEGPRSSENDDATA";

    //保存设置设备时间的数据命令
    public static  String SET_DEVICE_TIME;

    public static String RED_DEVICE_TIME="GDTIMER";

    //读取原始设备数据记录信息指令
    public static String COPY_DEVICE_DATA="GDRECORDXXR";

    //读取设备的统一编码
    public static String COPY_DEVICE_ID="GDIDR";

    //根据时间段读取设备里面的数据
    public static String RED_DEVICE_DATA_BY_TIME;

    //蓝牙APP启动拷贝数据记录命令
    public static String WRITE_NEW_DEVICE_CMD="GDRECORDBCOPY";

    //给新设备写入时间数据
    public static String WIRTE_NEW_DEVICE_TIME;

    //给新设备写入统一编码数据
    public static String WIRTE_NEW_DEVICE_CODE;

    //给新设备写入大量数据
    public static String WRITE_NEW_DEVICE_LONG_DATA;

    //设置北斗的中心号码
    public static String SET_CENTER_MOBILE;

    //获取设备型号
    public static String GET_DEVICE_MODEL="GDXHR";

    //让设备通过北斗方式发送实时数据
    public static String BEI_DOU_FANG_SHI_SEND_DATA="GDBLEBDSENDDATA";

    //读取设备北斗信号强度
    public static String RED_BEI_DOU_XIN_HAO_QIANG_DU="GDBDSQ";

    //读取本地的txt文档数据，下发给设备
    public static String SEND_TXT_CONTENT;

    //根据时间段读取设备里面的数据
    public static String RED_DEVICE_DATA_BY_TIME2;

    //读取采集路数
    public static String RED_CAIJI_ROAD="GDMETERNUMR";

    //根据路数读取实时数据
    public static String RED_TIME_DATA_BY_ROAD;

    //读取多路参数设置里面的统一编码
    public static String RED_MORE_SETTING_CODE;

    //读取多路参数设置里面的探头埋深
    public static String RED_MORE_SETTING_TANTOU;

    //设置采集路数
    public static String SET_CAIJI_ROAD;

    //设置多路参数的统一编码
    public static String SET_MORE_SETTING_CODE;

    //设置多路参数的探头埋深
    public static String SET_MORE_SETTING_TANTOU;

    //读取多路参数的探头ID号
    public static String RED_TANTOU_ID="GD&#IDR";

    //设置多路参数的探头ID号
    public static String SET_TANTOU_ID;

    //多路参数界面，读取SIM北斗数据
    public static String RED_MORE_SETTING_SIM;

    //多路参数中设置SIM北斗数据
    public static String SET_MORE_SETTING_SIM;

    //多路参数界面，单独读取SIM北斗数据
    public static String RED_MORE_SETTING_SIM2;

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
     * 新版设备，设置统一编码
     * @param code
     */
    public static void set_new_code(String code){
        StringBuffer stringBuffer=new StringBuffer("GDIDW");
        stringBuffer.append(code);
        SET_CODE_PHONE=stringBuffer.toString();
    }


    /**
     * 新版设备，设置SIM卡号
     * @param sim
     * @param data
     */
    public static void set_new_Sim(String sim,String data){
        StringBuffer stringBuffer=new StringBuffer();
        String[] strings=data.split(";");
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
     * 设置探头埋深2
     * @param data
     */
    public static void sendSetTanTou2(String data){
        StringBuffer stringBuffer=new StringBuffer("GDLINEW");
        stringBuffer.append(data.substring(0,1));

        data=data.replace("+","").replace("-","");
        int position=data.indexOf(".");
        if(position==-1) {
            for (int i=0;i<4-data.length();i++){
                stringBuffer.append("0");
            }
            stringBuffer.append(data+".0000");
        }else{
            final int hou=data.length() - position - 1;
            final int qian=data.length()-hou-1;
            for (int i=0;i<4-qian;i++){
                stringBuffer.append("0");
            }
            stringBuffer.append(data);
            for (int i=0;i<4-hou;i++){
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
     * 新版-采集频率-拼接要发送的数据
     * @param startTime
     * @param totalMinute
     */
    public static void new_sendCaiJi(String startTime,String totalMinute){
        StringBuffer stringBuffer=new StringBuffer("GDREADW"+startTime+",");
        stringBuffer.append(append(4,totalMinute));
        SET_CAI_JI_PIN_LU=stringBuffer.toString();
    }


    /**
     * 设置设备的时间
     * @param time
     */
    public static void sendDeviceTime(String time){
        StringBuffer stringBuffer=new StringBuffer("GDTIMEW");
        stringBuffer.append(time.substring(2, time.length()).replace("-","").replace(" ","").replace(":",""));
        SET_DEVICE_TIME=stringBuffer.toString();
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
     * 新版设置发送频率
     */
    public static void new_setFaSong(String time, String minute, String grps, String sendNum, Map<Integer,Integer> map){
        StringBuffer stringBuffer=new StringBuffer("GDSENDW");
        stringBuffer.append(time.substring(0,4));
        stringBuffer.append(time.substring(5,7));
        stringBuffer.append(time.substring(8,10));
        stringBuffer.append(time.substring(11,13));
        stringBuffer.append(time.substring(14,16));
        stringBuffer.append("00,");
        //追加发送间隔时间
        stringBuffer.append(append(4,minute)+",");
        //追加gprs模式,补发次数
        stringBuffer.append(grps+","+sendNum);
        SET_FA_SONG_PIN_LU=stringBuffer.toString();
        if(Integer.parseInt(sendNum)>0){
            stringBuffer.append(",");
            for (int i=0;i<map.size();i++){
                 stringBuffer.append(append(4,String.valueOf(map.get(i)))+",");
            }
            SET_FA_SONG_PIN_LU=stringBuffer.substring(0,stringBuffer.length()-1);
        }
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
        for(int i=0;i<3-index;i++){
            stringBuffer.append("0");
        }
        stringBuffer.append(result);

        //判断小数点后面几位
        for(int i=0;i<2-(result.length()-index-1);i++){
            stringBuffer.append("0");
        }
        SEND_DATA_SHUI_WEN=stringBuffer.toString();
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


    /**
     *设置根据时间段读取设备里面的数据
     */
    public static void redDeviceByTime(String startTime,String endTime){
        if(TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)){
            ToastUtil.showLong("读取的时间段有误");
            return;
        }
        RED_DEVICE_DATA_BY_TIME="GDRECORDA"+startTime.substring(2,startTime.length())+","+endTime.substring(2,endTime.length());
    }

    /**
     * 设置中心号码的命令
     */
    public static void setCenterSIM(String mobile){
        StringBuffer stringBuffer=new StringBuffer("GDBDCEN");
        //数据不够，用0补齐
        if(mobile.length()<7){
            mobile=append(7,mobile);
        }
        stringBuffer.append(mobile);
        SET_CENTER_MOBILE=stringBuffer.toString();
        LogUtils.e(SET_CENTER_MOBILE+"+++++++++++++=");

    }


    /**
     * 设置中心号码的命令
     */
    public static void setCenterSIM2(String totalData,String oldEditData,String mobile1,String mobile2,String mobile3){
        StringBuffer stringBuffer=new StringBuffer(totalData);
        //数据不够，用0补齐
        if(mobile1.length()<11){
            mobile1=append(11,mobile1);
        }
        if(mobile2.length()<11){
            mobile2=append(11,mobile2);
        }
        if(mobile3.length()<11){
            mobile3=append(11,mobile3);
        }
        SET_CENTER_MOBILE=stringBuffer.toString().replace(oldEditData,"CEN"+mobile1+","+mobile2+","+mobile3);

    }


    /**
     * 读取本地的txt文档内容发送设备
     */
    public static void sendTxtContent(String head,String content){
        SEND_TXT_CONTENT=head+content;
        LogUtils.e(SEND_TXT_CONTENT+"+++++++++++++++++++a");
    }


    /**
     *设置根据时间段读取设备里面的数据
     */
    public static void redDeviceByTime2(String startTime,String endTime){
        if(TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)){
            ToastUtil.showLong("读取的时间段有误");
            BuglyUtils.uploadBleMsg("读取的时间段有误：");
            return;
        }
        RED_DEVICE_DATA_BY_TIME2="GDRECORDC"+startTime.substring(2,startTime.length())+","+endTime.substring(2,endTime.length());
    }



    /**
     * 根据路数读取实时数据
     */
    public static void redTimeDataByRoad(int road){
        StringBuilder sb=new StringBuilder("GDNCURRENT");
        if(road<10){
            sb.append("0"+road);
        }else{
            sb.append(road);
        }
        RED_TIME_DATA_BY_ROAD=sb.toString();
    }


    /**
     * 读取多路参数设置里面的统一编码
     */
    public static void setRedMoreSettingCode(int road){
        StringBuilder sb=new StringBuilder("GDNIDR");
        if(road<10){
            sb.append("0"+road);
        }else{
            sb.append(road);
        }
        RED_MORE_SETTING_CODE=sb.toString();
    }


    /**
     * 读取多路参数设置里面的探头埋深
     */
    public static void setRedMoreSettingTanTou(int road){
        StringBuilder sb=new StringBuilder("GDNLINER");
        if(road<10){
            sb.append("0"+road);
        }else{
            sb.append(road);
        }
        RED_MORE_SETTING_TANTOU=sb.toString();
    }


    /**
     * 设置采集路数
     */
    public static void setSetCaiJiRoad(int m,String nn){
        StringBuilder sb=new StringBuilder("GDMETERNUMW");
        sb.append(m+","+nn);
        SET_CAIJI_ROAD=sb.toString();
    }


    /**
     * 设置多路参数的统一编码
     */
    public static void setSetMoreSettingCode(int road,MoreCode moreCode){
        StringBuilder sb=new StringBuilder("GDNIDW");
        if((road+1)<10){
            sb.append("0"+(road+1)+",");
        }else{
            sb.append((road+1)+",");
        }
        sb.append(append(15,moreCode.getCode()));

        sb.append(","+append(7,moreCode.getOther()));
        SET_MORE_SETTING_CODE=sb.toString();

        BuglyUtils.uploadBleMsg("设置的统一编码数据："+SET_MORE_SETTING_CODE);
    }


    /**
     * 设置多路参数的统一编码
     */
    public static void setSetMoreSettingTanTou(int road, MoreTanTou moreTanTou){
        StringBuilder sb=new StringBuilder("GDNLINEW");
        if((road+1)<10){
            sb.append("0"+(road+1)+",");
        }else{
            sb.append((road+1)+",");
        }

        String maishe=moreTanTou.getMaishen();
        sb.append(maishe.substring(0,1));

        maishe=maishe.replace("+","").replace("-","");

        //小数点前后都是4位，否则用0补齐
        sb.append(strAppend(maishe,4,4));

        sb.append(","+moreTanTou.getMidu()+","+moreTanTou.getPianyi());
        SET_MORE_SETTING_TANTOU=sb.toString();

    }


    /**
     * 设置探头id号
     * @param old
     * @param news
     */
    public static void setTanTouID(String old,String news){
        StringBuilder sb=new StringBuilder("GD&>#IDW"+old+"T");
        sb.append(append(3,news));
        SET_TANTOU_ID=sb.toString();
        BuglyUtils.uploadBleMsg("设置探头id号："+SET_TANTOU_ID);
    }


    /**
     * 多路参数界面，读取SIM北斗数据
     * @param data
     */
    public static void setRedMoreSettingSim(int data){
        StringBuilder sb=new StringBuilder("GDNTIMESIMR");
        if(data<10){
            sb.append("0"+data);
        }else{
            sb.append(data);
        }
        RED_MORE_SETTING_SIM=sb.toString();
    }


    /**
     * 多路参数中设置SIM北斗数据
     * @param num
     * @param sim
     */
    public static void setSetMoreSettingSim(String num,String sim){
        StringBuilder sb=new StringBuilder("GDNTIMESIMW"+num+",");
        sb.append(append(7,sim));
        SET_MORE_SETTING_SIM=sb.toString();
    }


    /**
     * 多路参数界面，单独读取SIM北斗数据
     * @param num
     */
    public static void setRedMoreSettingSim2(String num){
        StringBuilder sb=new StringBuilder("GDNTIMESIMR"+num);
        RED_MORE_SETTING_SIM2=sb.toString();
    }




    public static String strAppend(String data,int before,int after){
        StringBuilder sb=new StringBuilder("");
        final String[] strs=data.split("\\.");
        sb.append(append(before,strs.length>0 ? strs[0] : ""));
        sb.append(".");
        sb.append(appendAfter(after,strs.length>1 ? strs[1] : ""));
        return sb.toString();
    }



    /**
     * 数据不够，用0补齐
     */
    public static String append(int num,String data){
        String str=data;
        for (int i=0;i<num-data.length();i++){
            str="0"+str;
        }
        return str;
    }


    /**
     * 数据后面不够，用0补齐
     */
    public static String appendAfter(int num,String data){
        String str=data;
        for (int i=0;i<num-data.length();i++){
            str=str+"0";
        }
        return str;
    }


    public static int bleCmdStatus;
    public static void sendBleData(Activity activity,int status){
        bleCmdStatus=status;
        switch (status){
            //读取设备版本号
            case BleContant.RED_DEVICE_VERSION:
                SendBleDataManager.getInstance().sendData(activity,RED_VERSION);
                break;
            //读取统一编码，SIM卡号
            case BleContant.SEND_GET_CODE_PHONE:
                 SendBleDataManager.getInstance().sendData(activity,GET_CODE_PHONE);
                 break;
            //读取探头埋深
            case BleContant.SEND_GET_TANTOU:
                 SendBleDataManager.getInstance().sendData(activity,GET_TANTOU);
                 break;
            //读取采集频率
            case BleContant.SEND_CAI_JI_PIN_LU:
                 SendBleDataManager.getInstance().sendData(activity,GET_CAI_JI_PIN_LU);
                 break;
            //读取发送频率
            case BleContant.SEND_FA_SONG_PIN_LU:
                 SendBleDataManager.getInstance().sendData(activity,GET_FA_SONG_PIN_LU);
                 break;
            //设置统一编码，SIM卡号
            case BleContant.SET_CODE_PHONE:
                 SendBleDataManager.getInstance().sendData(activity,SET_CODE_PHONE);
                 break;
            //设置探头埋深
            case BleContant.SET_TANTOU:
                 SendBleDataManager.getInstance().sendData(activity,SET_TANTOU);
                 break;
            //设置采集频率
            case BleContant.SET_CAI_JI_PIN_LU:
                 SendBleDataManager.getInstance().sendData(activity,SET_CAI_JI_PIN_LU);
                 break;
             //设置发送频率
            case BleContant.SET_FA_SONG:
                 SendBleDataManager.getInstance().sendData(activity,SET_FA_SONG_PIN_LU);
                 break;
            //设置误差数据
            case BleContant.SET_DATA_CHECK:
                 SendBleDataManager.getInstance().sendData(activity,SET_DATA_CHECK);
                 break;
            //查询实时数据
            case BleContant.SEND_REAL_TIME_DATA:
                 SendBleDataManager.getInstance().sendData(activity,SEND_REAL_TIME_DATA);
                 break;
            //设置网络数据
            case BleContant.SET_IP_PORT:
                 SendBleDataManager.getInstance().sendData(activity,SET_IP_PORT);
                 break;
            //校测前先读取水位偏移量
            case BleContant.SEND_CHECK_ERROR:
                 SendBleDataManager.getInstance().sendData(activity,SEND_CHECK_ERROR);
                 break;
            //校测前先读取水温偏移量
            case BleContant.RED_SHUI_WEN_PYL:
                  SendBleDataManager.getInstance().sendData(activity,RED_SHUI_WEN_PYL);
                  break;
            //设置水温误差数据
            case BleContant.SEND_DATA_SHUI_WEN:
                  SendBleDataManager.getInstance().sendData(activity,SEND_DATA_SHUI_WEN);
                  break;
            //校测前先读取电导率偏移量
            case BleContant.RED_DIAN_DAO_LV_PYL:
                  SendBleDataManager.getInstance().sendData(activity,RED_DIAN_DAO_LV_PYL);
                  break;
            //设置水温电导率数据
            case BleContant.SEND_DATA_DIAN_DAO_LV:
                 SendBleDataManager.getInstance().sendData(activity,SEND_DATA_DIAN_DAO_LV);
                 break;
            //读取设备记录
            case BleContant.RED_DEVICE_RECOFD:
                  SendBleDataManager.getInstance().sendData(activity,RED_DEVOCE_RECORD);
                  break;
            //新设备读取统一编码
            case BleContant.RED_NEW_GET_CODE:
                  SendBleDataManager.getInstance().sendData(activity,RED_NEW_GET_CODE);
                  break;
            //发送数据菜单：发送数据
            case BleContant.MENU_SEND_DATA:
                  SendBleDataManager.getInstance().sendData(activity,MENU_SEND_DATA);
                  break;
            //设置设备时间
            case BleContant.SEND_DEVICE_TIME:
                  SendBleDataManager.getInstance().sendData(activity,SET_DEVICE_TIME);
                  break;
            //读取设备的时间
            case BleContant.RED_DEVICE_TIME:
                  SendBleDataManager.getInstance().sendData(activity,RED_DEVICE_TIME);
                  break;
            //读取原始设备数据记录信息指令
            case BleContant.COPY_DEVICE_DATA:
                  SendBleDataManager.getInstance().sendData(activity,COPY_DEVICE_DATA);
                  break;
            //读取设备的统一编码
            case BleContant.COPY_DEVICE_ID:
                  SendBleDataManager.getInstance().sendData(activity,COPY_DEVICE_ID);
                  break;
            //根据时间段读取设备里面的数据
            case BleContant.RED_DEVICE_DATA_BY_TIME:
                  SendBleDataManager.getInstance().sendData(activity,RED_DEVICE_DATA_BY_TIME);
                  break;
            //蓝牙APP启动拷贝数据记录命令
            case BleContant.WRITE_NEW_DEVICE_CMD:
                  SendBleDataManager.getInstance().sendData(activity,WRITE_NEW_DEVICE_CMD);
                  break;
            //给新设备写入时间数据
            case BleContant.WIRTE_NEW_DEVICE_TIME:
                  SendBleDataManager.getInstance().sendData(activity,WIRTE_NEW_DEVICE_TIME);
                  break;
            //给新设备写入统一编码数据
            case BleContant.WIRTE_NEW_DEVICE_CODE:
                  SendBleDataManager.getInstance().sendData(activity,WIRTE_NEW_DEVICE_CODE);
                  break;
            //给新设备写入大量数据
            case BleContant.WRITE_NEW_DEVICE_LONG_DATA:
                  SendBleDataManager.getInstance().sendData(activity,WRITE_NEW_DEVICE_LONG_DATA);
                  break;
            //设置北斗的中心号码
            case BleContant.SET_CENTER_MOBILE:
                 SendBleDataManager.getInstance().sendData(activity,SET_CENTER_MOBILE);
                  break;
            //获取设备型号
            case BleContant.GET_DEVICE_MODEL:
                  SendBleDataManager.getInstance().sendData(activity,GET_DEVICE_MODEL);
                  break;
            //让设备通过北斗方式发送实时数据
            case BleContant.BEI_DOU_FANG_SHI_SEND_DATA:
                  SendBleDataManager.getInstance().sendData(activity,BEI_DOU_FANG_SHI_SEND_DATA);
                  break;
            //读取设备北斗信号强度
            case BleContant.RED_BEI_DOU_XIN_HAO_QIANG_DU:
                  SendBleDataManager.getInstance().sendData(activity,RED_BEI_DOU_XIN_HAO_QIANG_DU);
                  break;
            //读取本地的txt文档数据，下发给设备
            case BleContant.SEND_TXT_CONTENT:
                SendBleDataManager.getInstance().sendData(activity,SEND_TXT_CONTENT);
                break;
            case BleContant.RED_DEVICE_DATA_BY_TIME2:
                 SendBleDataManager.getInstance().sendData(activity,RED_DEVICE_DATA_BY_TIME2);
                 break;
            //读取采集路数
            case BleContant.RED_CAIJI_ROAD:
                 SendBleDataManager.getInstance().sendData(activity,RED_CAIJI_ROAD);
                 break;
            //根据路数读取实时数据
            case BleContant.RED_TIME_DATA_BY_ROAD:
                 SendBleDataManager.getInstance().sendData(activity,RED_TIME_DATA_BY_ROAD);
                 break;
            //读取多路参数设置里面的统一编码
            case BleContant.RED_MORE_SETTING_CODE:
                 SendBleDataManager.getInstance().sendData(activity,RED_MORE_SETTING_CODE);
                 break;
            //读取多路参数设置里面的探头埋深
            case BleContant.RED_MORE_SETTING_TANTOU:
                SendBleDataManager.getInstance().sendData(activity,RED_MORE_SETTING_TANTOU);
                break;
            //设置采集路数
            case BleContant.SET_CAIJI_ROAD:
                SendBleDataManager.getInstance().sendData(activity,SET_CAIJI_ROAD);
                break;
            //设置多路参数的统一编码
            case BleContant.SET_MORE_SETTING_CODE:
                SendBleDataManager.getInstance().sendData(activity,SET_MORE_SETTING_CODE);
                break;
            //设置多路参数的探头埋深
            case BleContant.SET_MORE_SETTING_TANTOU:
                SendBleDataManager.getInstance().sendData(activity,SET_MORE_SETTING_TANTOU);
                break;
            //读取多路参数的探头ID号
            case BleContant.RED_TANTOU_ID:
                SendBleDataManager.getInstance().sendData(activity,RED_TANTOU_ID);
                break;
            //设置多路参数的探头ID号
            case BleContant.SET_TANTOU_ID:
                SendBleDataManager.getInstance().sendData(activity,SET_TANTOU_ID);
                break;
            //多路参数界面，读取SIM北斗数据
            case BleContant.RED_MORE_SETTING_SIM:
                SendBleDataManager.getInstance().sendData(activity,RED_MORE_SETTING_SIM);
                break;
            //多路参数界面，单独读取SIM北斗数据
            case BleContant.RED_MORE_SETTING_SIM2:
                SendBleDataManager.getInstance().sendData(activity,RED_MORE_SETTING_SIM2);
                break;
             default:
                 break;

        }
    }
}
