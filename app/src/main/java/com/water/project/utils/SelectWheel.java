package com.water.project.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.water.project.R;
import com.water.project.callback.SelectCallBack;
import com.water.project.view.CycleWheelView;

import java.util.ArrayList;
import java.util.List;

public class SelectWheel {

    public static void initWheel(CycleWheelView wheel, int size){
        wheel.setSelection(0);
        try {
            wheel.setWheelSize(size);
        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
        wheel.setCycleEnable(false);
        wheel.setAlphaGradual(0.5f);
        wheel.setDivider(Color.parseColor("#00B9ED"),1);
        wheel.setSolid(Color.WHITE, Color.WHITE);
        wheel.setLabelColor(Color.GRAY);
        wheel.setLabelSelectColor(Color.BLACK);
    }


    /**
     * 选择天线型号
     * @param activity
     */
    public static void selectAntenna(Activity activity, final SelectCallBack selectCallBack){
        View view= LayoutInflater.from(activity).inflate(R.layout.min_wheel_select,null);
        final PopupWindow popupWindow= DialogUtils.showPopWindow(view);
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0,0);
        final CycleWheelView wheel=view.findViewById(R.id.wheel);

        List<String> list=new ArrayList<>();
        list.add("PD");list.add("BDRGB");list.add("其他");

        wheel.setLabels(list);
        initWheel(wheel,3);

        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                selectCallBack.getSelect(wheel.getSelectLabel(),null);
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.rel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }



    /**
     * 设置北斗接收数据等待时间
     * @param activity
     */
    public static void selectWaitTime(Activity activity, final SelectCallBack selectCallBack){
        View view= LayoutInflater.from(activity).inflate(R.layout.wheel_select,null);
        final PopupWindow popupWindow= DialogUtils.showPopWindow(view);
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0,0);
        final CycleWheelView wheel=view.findViewById(R.id.wheel);

        List<String> list=new ArrayList<>();
        list.add("2秒");list.add("3秒");list.add("4秒");list.add("5秒");list.add("6秒");list.add("7秒");list.add("8秒");list.add("9秒");list.add("10秒");

        wheel.setLabels(list);
        initWheel(wheel,5);

        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
                selectCallBack.getSelect(wheel.getSelectLabel(),null);
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.rel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

}
