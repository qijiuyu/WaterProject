package com.water.project.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SelectTimeUtils {

    /**
     * 获取年
     * @return
     */
    public static List<String> getYear(){
        List<String> list=new ArrayList<>();
        for(int i=1990;i<2060;i++){
            list.add(i+"年");
        }
        return list;
    }


    /**
     * 获取月
     * @return
     */
    public static List<String> getMonth(){
        List<String> list=new ArrayList<>();
        list.add("01月");list.add("02月");list.add("03月");list.add("04月");list.add("05月");list.add("06月");list.add("07月");
        list.add("08月");list.add("09月");list.add("10月");list.add("11月");list.add("12月");
        return list;
    }

    /**
     * 获取当月的 天数
     */
    public static int getCurrentMonthDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 根据 年、月 获取对应的月份 的 天数
     */
    public static int getDaysByYearMonth(int year, int month)
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }


    /**
     * 获取天
     * @return
     */
    public static List<String> getDay(){
        List<String> list=new ArrayList<>();
        final int maxDay=getCurrentMonthDay();
        for (int i=1;i<=maxDay;i++){
             if(i<10){
                 list.add("0"+i+"日");
             }else{
                 list.add(i+"日");
             }
        }
        return list;
    }


    /**
     * 获取小时
     * @return
     */
    public static List<String> getHour(){
        List<String> list=new ArrayList<>();
        for (int i=0;i<24;i++){
            if(i<10){
                list.add("0"+i+"时");
            }else{
                list.add(i+"时");
            }
        }
        return list;
    }


    /**
     * 获取分钟
     * @return
     */
    public static List<String> getMinute(){
        List<String> list=new ArrayList<>();
        for (int i=0;i<60;i++){
             if(i<10){
                 list.add("0"+i+"分");
             }else{
                 list.add(i+"分");
             }
        }
        return list;
    }


    /**
     * 获取分钟
     * @return
     */
    public static List<String> getSeconds(){
        List<String> list=new ArrayList<>();
        for (int i=0;i<60;i++){
            if(i<10){
                list.add("0"+i+"秒");
            }else{
                list.add(i+"秒");
            }
        }
        return list;
    }


    /**
     * 获取gprs模式数据
     * @return
     */
    public static List<String> getGPRS(){
        List<String> list=new ArrayList<>();
        for (int i=1;i<11;i++){
            if(i<10){
                list.add("0"+i);
            }else{
                list.add(String.valueOf(i));
            }
        }
        return list;
    }


    /**
     * 获取补发次数数据
     * @return
     */
    public static List<String> getSendNum(){
        List<String> list=new ArrayList<>();
        for (int i=0;i<21;i++){
            if(i<10){
                list.add("0"+i);
            }else{
                list.add(String.valueOf(i));
            }
        }
        return list;
    }
}
