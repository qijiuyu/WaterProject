package com.water.project.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/9 0009.
 */

public class ActivitysLifecycle implements Application.ActivityLifecycleCallbacks {
    private List<Activity> activitys =  new LinkedList();
    private static ActivitysLifecycle activitysLifecycle;

    public static ActivitysLifecycle getInstance(){
        if(null==activitysLifecycle){
            activitysLifecycle=new ActivitysLifecycle();
        }
        return activitysLifecycle;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activitys != null && activitys.size() > 0) {
            if(!activitys.contains(activity)){
                activitys.add(activity);
            }
        }else{
            activitys.add(activity);
        }

    }

    // 遍历所有Activity并finish
    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                if(null!=activity){
                    activity.finish();
                }
            }
        }
        System.exit(0);
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
