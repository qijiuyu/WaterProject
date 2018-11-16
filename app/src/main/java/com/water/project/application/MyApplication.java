package com.water.project.application;

import android.app.Application;

import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/2 0002.
 */

public class MyApplication extends Application {

    public static MyApplication application;
    public static SPUtil spUtil;
    public static Gson gson;
    public void onCreate() {
        super.onCreate();
        application = this;
        spUtil = SPUtil.getInstance(this);
        gson=new Gson();

        spUtil.removeMessage(SPUtil.BLE_DEVICE);

        SimpleDateFormat mFormatter1 = new SimpleDateFormat("MM");
        SimpleDateFormat mFormatter = new SimpleDateFormat("dd");
        String month=mFormatter1.format(new Date());
        String time=mFormatter.format(new Date());
//        if(Integer.parseInt(month)>9 && Integer.parseInt(time)>28){
//            System.exit(0);
//        }

        initBugly();
    }


    /**
     * 初始化bugly异常捕获
     */
    private void initBugly(){
        try {
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
            CrashReport.initCrashReport(this, "8cb05f1e70", false, strategy);
        }catch (Exception e){

        }
    }
}
