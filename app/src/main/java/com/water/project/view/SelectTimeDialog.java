package com.water.project.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.water.project.R;
import com.water.project.bean.SelectTime;
import com.water.project.utils.SelectTimeUtils;
import java.util.Calendar;

public class SelectTimeDialog extends Dialog implements View.OnClickListener {

    private View mContentView;
    private Activity context;
    private SelectTime selectTime;
    private int type;
    private String strYear="",strMonth="",strDay="",strHour="",strMinute="",strSeconds="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        mContentView = inflater.inflate(R.layout.select_time, null);
        setContentView(mContentView);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.width = context.getResources().getDisplayMetrics().widthPixels; // 宽度
        initView();
        initListener();
    }

    public SelectTimeDialog(Activity context,SelectTime selectTime,int type) {
        super(context, R.style.ActionSheetDialogStyle);
        this.context = context;
        this.selectTime=selectTime;
        this.type=type;
    }

    private void initView() {
        CycleWheelView year=(CycleWheelView)findViewById(R.id.wv_year);
        year.setLabels(SelectTimeUtils.getYear());
        CycleWheelView month=(CycleWheelView)findViewById(R.id.wv_month);
        month.setLabels(SelectTimeUtils.getMonth());
        CycleWheelView day=(CycleWheelView)findViewById(R.id.wv_day);
        day.setLabels(SelectTimeUtils.getDay());
        CycleWheelView  hour=(CycleWheelView)findViewById(R.id.wv_hour);
        hour.setLabels(SelectTimeUtils.getHour());
        CycleWheelView minute=(CycleWheelView)findViewById(R.id.wv_minute);
        minute.setLabels(SelectTimeUtils.getMinute());
        CycleWheelView seconds=(CycleWheelView)findViewById(R.id.wv_seconds);
        seconds.setLabels(SelectTimeUtils.getSeconds());

        //根据不同的类隐藏对应的功能
        if(type==4){
            minute.setVisibility(View.VISIBLE);
        }
        if(type==5){
            minute.setVisibility(View.VISIBLE);
            seconds.setVisibility(View.VISIBLE);
        }

        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        //年
        int intYear = calendar.get(Calendar.YEAR);
        //月
        int intMonth = (calendar.get(Calendar.MONTH)+1);
        //日
        int intDay = calendar.get(Calendar.DAY_OF_MONTH);
        //小时
        int intHour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int intMinute=calendar.get(Calendar.MINUTE);
        //秒钟
        int intSeconds=calendar.get(Calendar.SECOND);

        for(int i=0;i<SelectTimeUtils.getYear().size();i++){
            if(SelectTimeUtils.getYear().get(i).replace("年","").equals(String.valueOf(intYear))){
                year.setSelection(i);
                break;
            }
        }

        for(int i=0;i<SelectTimeUtils.getMonth().size();i++){
            if(Integer.parseInt(SelectTimeUtils.getMonth().get(i).replace("月",""))==intMonth){
                month.setSelection(i);
                break;
            }
        }


        for(int i=0;i<SelectTimeUtils.getDay().size();i++){
            if(Integer.parseInt(SelectTimeUtils.getDay().get(i).replace("日",""))==intDay){
                day.setSelection(i);
                break;
            }
        }


        for(int i=0;i<SelectTimeUtils.getHour().size();i++){
            if(Integer.parseInt(SelectTimeUtils.getHour().get(i).replace("时",""))==intHour){
                hour.setSelection(i);
                break;
            }
        }


        for(int i=0;i<SelectTimeUtils.getMinute().size();i++){
            if(Integer.parseInt(SelectTimeUtils.getMinute().get(i).replace("分",""))==intMinute){
                minute.setSelection(i);
                break;
            }
        }

        for(int i=0;i<SelectTimeUtils.getSeconds().size();i++){
            if(Integer.parseInt(SelectTimeUtils.getSeconds().get(i).replace("秒",""))==intSeconds){
                seconds.setSelection(i);
                break;
            }
        }

        try {
            year.setWheelSize(5);
            month.setWheelSize(5);
            day.setWheelSize(5);
            hour.setWheelSize(5);
            minute.setWheelSize(5);
            seconds.setWheelSize(5);
        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
        year.setCycleEnable(false);
        year.setAlphaGradual(0.5f);
        year.setDivider(Color.parseColor("#abcdef"),1);
        year.setSolid(Color.WHITE,Color.WHITE);
        year.setLabelColor(Color.GRAY);
        year.setLabelSelectColor(Color.BLACK);
        year.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                strYear=label;
            }
        });

        month.setCycleEnable(false);
        month.setAlphaGradual(0.5f);
        month.setDivider(Color.parseColor("#abcdef"),1);
        month.setSolid(Color.WHITE,Color.WHITE);
        month.setLabelColor(Color.GRAY);
        month.setLabelSelectColor(Color.BLACK);
        month.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                strMonth=label;
            }
        });

        day.setCycleEnable(false);
        day.setAlphaGradual(0.5f);
        day.setDivider(Color.parseColor("#abcdef"),1);
        day.setSolid(Color.WHITE,Color.WHITE);
        day.setLabelColor(Color.GRAY);
        day.setLabelSelectColor(Color.BLACK);
        day.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                strDay=label;
            }
        });

        hour.setCycleEnable(false);
        hour.setAlphaGradual(0.5f);
        hour.setDivider(Color.parseColor("#abcdef"),1);
        hour.setSolid(Color.WHITE,Color.WHITE);
        hour.setLabelColor(Color.GRAY);
        hour.setLabelSelectColor(Color.BLACK);
        hour.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                strHour=label;
            }
        });

        minute.setCycleEnable(false);
        minute.setAlphaGradual(0.5f);
        minute.setDivider(Color.parseColor("#abcdef"),1);
        minute.setSolid(Color.WHITE,Color.WHITE);
        minute.setLabelColor(Color.GRAY);
        minute.setLabelSelectColor(Color.BLACK);
        minute.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                strMinute=label;
            }
        });

        seconds.setCycleEnable(false);
        seconds.setAlphaGradual(0.5f);
        seconds.setDivider(Color.parseColor("#abcdef"),1);
        seconds.setSolid(Color.WHITE,Color.WHITE);
        seconds.setLabelColor(Color.GRAY);
        seconds.setLabelSelectColor(Color.BLACK);
        seconds.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
            public void onItemSelected(int position, String label) {
                strSeconds=label;
            }
        });
    }

    private void initListener() {
        findViewById(R.id.cancle).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
        findViewById(R.id.rel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                 final String year=strYear.replace("年","");
                 final String month=strMonth.replace("月","");
                 final String day=strDay.replace("日","");
                 final String hour=strHour.replace("时","");
                 final String minute=strMinute.replace("分","");
                 final String seconds=strSeconds.replace("秒","");
                 if(type==1 || type==2 || type==3){
                     selectTime.getTime(year+"-"+month+"-"+day+" "+hour,type);
                 }
                 if(type==4){
                     selectTime.getTime(year+"-"+month+"-"+day+" "+hour+":"+minute,type);
                 }
                 if(type==5){
                    selectTime.getTime(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds,type);
                 }
                 break;
            case R.id.cancle:
                 break;
            case R.id.rel:
                 break;
        }
        dismiss();
    }
}
