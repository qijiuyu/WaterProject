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

/**
 * 故障保修，联系客服，使用说明等功能
 * Created by lyn on 2017/3/14.
 */

public class SelectTimeDialog extends Dialog implements View.OnClickListener {

    private View mContentView;
    private Activity context;
    private CycleWheelView year,month,day,hour;
    private SelectTime selectTime;
    private int type;
    private String strYear,strMonth,strDay,strHour;
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
        year=(CycleWheelView)findViewById(R.id.wv_year);
        year.setLabels(SelectTimeUtils.getYear());
        month=(CycleWheelView)findViewById(R.id.wv_month);
        month.setLabels(SelectTimeUtils.getMonth());
        day=(CycleWheelView)findViewById(R.id.wv_day);
        day.setLabels(SelectTimeUtils.getDay());
        hour=(CycleWheelView)findViewById(R.id.wv_hour);
        hour.setLabels(SelectTimeUtils.getHour());

        Calendar calendar = Calendar.getInstance();
        //年
        int intYear = calendar.get(Calendar.YEAR);
        //月
        int intMonth = (calendar.get(Calendar.MONTH)+1);
        //日
        int intDay = calendar.get(Calendar.DAY_OF_MONTH);
        //小时
        int intHour = calendar.get(Calendar.HOUR_OF_DAY);

        for(int i=0;i<SelectTimeUtils.getYear().size();i++){
            if(SelectTimeUtils.getYear().get(i).replace("年","").equals(String.valueOf(intYear))){
                year.setSelection(i);
                break;
            }
        }

        for(int i=0;i<SelectTimeUtils.getMonth().size();i++){
            if(intMonth<10 && String.valueOf(intMonth).length()==1){
                if(SelectTimeUtils.getMonth().get(i).replace("月","").equals("0"+intMonth)){
                    month.setSelection(i);
                    break;
                }
            }else{
                if(SelectTimeUtils.getMonth().get(i).replace("月","").equals(String.valueOf(intMonth))){
                    month.setSelection(i);
                    break;
                }
            }
        }


        for(int i=0;i<SelectTimeUtils.getDay().size();i++){
            if(intDay<10 && String.valueOf(intDay).length()==1){
                if(SelectTimeUtils.getDay().get(i).replace("日","").equals("0"+intDay)){
                    day.setSelection(i);
                    break;
                }
            }else{
                if(SelectTimeUtils.getDay().get(i).replace("日","").equals(String.valueOf(intDay))){
                    day.setSelection(i);
                    break;
                }
            }
        }


        for(int i=0;i<SelectTimeUtils.getHour().size();i++){
            if(intHour<10 && String.valueOf(intHour).length()==1){
                if(SelectTimeUtils.getHour().get(i).replace("时","").equals("0"+intHour)){
                    hour.setSelection(i);
                    break;
                }
            }else{
                if(SelectTimeUtils.getHour().get(i).replace("时","").equals(String.valueOf(intHour))){
                    hour.setSelection(i);
                    break;
                }
            }
        }

        try {
            year.setWheelSize(5);
            month.setWheelSize(5);
            day.setWheelSize(5);
            hour.setWheelSize(5);
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
                 selectTime.getTime(year+"-"+month+"-"+day+" "+hour+":00",type);
                 break;
            case R.id.cancle:
                 break;
            case R.id.rel:
                 break;
        }
        dismiss();
    }
}
