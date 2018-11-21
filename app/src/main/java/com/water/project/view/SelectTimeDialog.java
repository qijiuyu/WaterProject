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

        try {
            year.setWheelSize(5);
            month.setWheelSize(5);
            day.setWheelSize(5);
            hour.setWheelSize(5);
        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
        year.setCycleEnable(false);
        year.setSelection(10);
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
        month.setSelection(10);
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
        day.setSelection(10);
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
        hour.setSelection(10);
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
