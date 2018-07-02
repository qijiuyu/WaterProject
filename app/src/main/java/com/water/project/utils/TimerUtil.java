package com.water.project.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器
 * Created by lyn on 2017/3/13.
 */

public class TimerUtil extends Timer {
    private TimerCallBack callTime;
    private long delay;
    private long period;

    /**
     * @param delay  延时执行
     * @param period 定时长
     */
    public TimerUtil(long delay, long period, TimerCallBack timerCallBack) {
        this.delay = delay;
        this.period = period;
        this.callTime = timerCallBack;
    }

    /**
     * @param delay 延时执行
     */
    public TimerUtil(long delay, TimerCallBack timerCallBack) {
        period = 0;
        this.delay = delay;
        this.callTime = timerCallBack;
    }

    public void start() {
        if(period!=0){
            this.schedule(new MyTime(), delay, period);
        }else{
            this.schedule(new MyTime(), delay);
        }
    }

    class MyTime extends TimerTask {
        public void run() {
            if (callTime == null) {
                return;
            }
            callTime.onFulfill();
        }
    }

    public interface TimerCallBack {
        void onFulfill();
    }

    public void stop(){
        TimerUtil.this.cancel();
        TimerUtil.this.purge();
    }
}
