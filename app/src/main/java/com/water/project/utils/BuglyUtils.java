package com.water.project.utils;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2018/1/10 0010.
 */

public class BuglyUtils {


    /**
     * 上报异常数据
     * @param msg
     */
    public static void uploadBleMsg(String msg){
        Throwable throwable=new Throwable(msg);
        CrashReport.postCatchedException(throwable);
    }

}
