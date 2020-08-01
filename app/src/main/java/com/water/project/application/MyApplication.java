package com.water.project.application;

import android.app.Application;
import android.content.Context;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;
import com.water.project.utils.ActivitysLifecycle;
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

        initBugly();

        //管理Activity
        registerActivityLifecycleCallbacks(ActivitysLifecycle.getInstance());
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
