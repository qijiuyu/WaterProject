package com.water.project.presenter;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.activity.GetRecordActivity;
import com.water.project.bean.SelectTime;
import com.water.project.utils.DialogUtils;
import com.water.project.utils.ToastUtil;
import com.water.project.utils.Util;
import com.water.project.view.SelectTimeDialog;

import java.text.SimpleDateFormat;

public class GetRecordPersenter {

    private GetRecordActivity activity;

    public GetRecordPersenter(GetRecordActivity activity){
        this.activity=activity;
    }

    /**
     * 读取第三条记录时，弹出开始与结束时间弹框
     * @param red1
     */
    //间隔分钟
    private int minutes;
    //总条数
    private int totalNum;
    //设备里数据的开始与结束时间---long型
    private long startLong,endLong;
    public void showDialogRed3(String red1){
        final String[] strings = red1.replace(" ","").split(",");
        strings[1] = "20"+strings[1].substring(0,2)+"-"+strings[1].substring(2,4)+"-"+strings[1].substring(4,6)+" "+strings[1].substring(6,8)+":"+strings[1].substring(8,10);
        strings[2] = "20"+strings[2].substring(0,2)+"-"+strings[2].substring(2,4)+"-"+strings[2].substring(4,6)+" "+strings[2].substring(6,8)+":"+strings[2].substring(8,10);
        //间隔分钟
        minutes = Integer.parseInt(strings[3]);

        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_set_red_time,null);
        final Dialog dialog= DialogUtils.dialogPop(view,activity);
        final TextView tvStart=view.findViewById(R.id.tv_start);
        final TextView tvEnd=view.findViewById(R.id.tv_end);
        tvStart.setText(strings[1]);
        tvEnd.setText(strings[2]);
        //选择开始时间
        tvStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SelectTimeDialog(activity, new SelectTime() {
                    public void getTime(String time, int type) {
                        if(Util.compare(time,strings[1])){
                            ToastUtil.showLong("设备数据记录的起始时间是"+strings[1]);
                            return;
                        }
                        tvStart.setText(time);
                    }
                },4).show();
            }
        });
        //选择结束时间
        tvEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SelectTimeDialog(activity, new SelectTime() {
                    public void getTime(String time, int type) {
                        if(!time.equals(strings[2]) && !Util.compare(time,strings[2])){
                            ToastUtil.showLong("设备数据记录的最新时间是"+strings[2]);
                            return;
                        }
                        tvEnd.setText(time);
                    }
                },4).show();
            }
        });

        //确定
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String startTime=tvStart.getText().toString().trim();
                String endTime=tvEnd.getText().toString().trim();
                if(startTime.equals(endTime)){
                    ToastUtil.showLong("开始时间与结束时间不能一致");
                    return;
                }
                //获取总条数
                totalNum = (getGapMinutes(startTime, endTime) / minutes) + 1;
                dialog.dismiss();
            }
        });
    }


    /**
     * 计算分钟差
     * @param startDate
     * @param endDate
     * @return
     */
    public int getGapMinutes(String startDate, String endDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            startLong = df.parse(startDate).getTime();
            endLong = df.parse(endDate).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        int minutes = (int) ((endLong - startLong) / (1000 * 60));
        return minutes;
    }
}
