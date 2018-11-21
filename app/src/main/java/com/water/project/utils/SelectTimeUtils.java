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
        list.add("00时");list.add("01时");list.add("02时");list.add("03时");list.add("04时");list.add("05时");list.add("06时");list.add("07时");list.add("08时");list.add("09时");list.add("10时");list.add("11时");
        list.add("12时");list.add("13时");list.add("14时");list.add("15时");list.add("16时");list.add("17时");list.add("18时");list.add("19时");list.add("20时");list.add("21时");list.add("22时");list.add("23时");
        return list;
    }
}
