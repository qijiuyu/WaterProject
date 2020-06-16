package com.water.project.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.water.project.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 判断字符串中是否包含字母
     * @param cardNum
     * @return
     */
    public static boolean judgeContainsStr(String cardNum) {
        String regex=".*[a-zA-Z]+.*";
        Matcher m= Pattern.compile(regex).matcher(cardNum);
        return m.matches();
    }


    /**
     * 判断字符串是否是数字
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    /**
     * 去掉字符串前面多余的0
     * @param str
     * @return
     */
    public static String delete_ling(String str){
        str=str.replaceAll("^(0+)", "");
        return str;
    }


    /**
     * 判断字符串是否是字母
     * @param fstrData
     * @return
     */
    public static  boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * dialog弹框
     *
     * @param view
     */
    public static Dialog dialogPop(Activity activity,View view) {
        Dialog baseDialog = new Dialog(activity, R.style.ActionSheetDialogStyle);
        baseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        baseDialog.setTitle(null);
        baseDialog.setCancelable(true);
        baseDialog.setContentView(view);
        Window window = baseDialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        baseDialog.show();
        return baseDialog;
    }


    /**
     * 判断两个时间大小
     * @param time1
     * @param time2
     * @return
     */
    public static boolean compare(String time1,String time2) {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date a = null,b= null;
        try {
            //将字符串形式的时间转化为Date类型的时间
            a=sdf.parse(time1);
            b=sdf.parse(time2);
        }catch (Exception e){
            e.printStackTrace();
        }
        //Date类的一个方法，如果a早于b返回true，否则返回false
        if(a.before(b)){
            return true;
        }
        else{
            return false;
        }
    }
}
