package com.water.project.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class Util extends ClassLoader {

    /**
     * 保留小数的double数据
     * @param d
     * @return
     */
    public static String setDouble(double d,int type){
        DecimalFormat df=null;
        switch (type){
            case 1:
                df = new DecimalFormat("0.0");
                break;
            case 2:
                df = new DecimalFormat("0.00");
                break;
            case 3:
                 df = new DecimalFormat("0.000");
                 break;
            case 4:
                df = new DecimalFormat("0.0000");
                 break;
        }
        return df.format(d);
    }

    /**
     * 获取当前系统的版本号
     *
     * @return
     */
    public static int getVersionCode(Context mContext) {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            int version = packInfo.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取当前系统的版本名称
     *
     * @return
     */
    public static String getVersionName(Context mContext) {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /*
     * 小数点后面必须保留三位小数
     * @param msg
     * @return
     */
    public static boolean getCharIndex(String msg){
        final int length=msg.length();
        final int strChar=msg.indexOf(".");
        if(length-strChar==4){
            return true;
        }else{
            return false;
        }
    }


    /**
     * double 相加
     * @param d1
     * @param d2
     * @return
     */
    public static double sum(double d1,double d2){
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }


    /**
     * double 相减
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(double d1,double d2){
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }

    /**
     * 获取小时数据
     * @return
     */
    public static List<String> getHourList(){
        List<String> list=new ArrayList<>();
        list.add("1小时");
        list.add("2小时");
        list.add("3小时");
        list.add("4小时");
        list.add("5小时");
        list.add("6小时");
        list.add("7小时");
        list.add("8小时");
        list.add("9小时");
        list.add("10小时");
        list.add("11小时");
        list.add("12小时");
        list.add("13小时");
        list.add("14小时");
        list.add("15小时");
        list.add("16小时");
        list.add("17小时");
        list.add("18小时");
        list.add("19小时");
        list.add("20小时");
        list.add("21小时");
        list.add("22小时");
        list.add("23小时");
        list.add("24小时");
        return list;
    }


    /**
     * 获取几点数据
     * @return
     */
    public static List<String> getDateList(){
        List<String> list=new ArrayList<>();
        list.add("1点");
        list.add("2点");
        list.add("3点");
        list.add("4点");
        list.add("5点");
        list.add("6点");
        list.add("7点");
        list.add("8点");
        list.add("9点");
        list.add("10点");
        list.add("11点");
        list.add("12点");
        list.add("13点");
        list.add("14点");
        list.add("15点");
        list.add("16点");
        list.add("17点");
        list.add("18点");
        list.add("19点");
        list.add("20点");
        list.add("21点");
        list.add("22点");
        list.add("23点");
        list.add("24点");
        return list;
    }
}
