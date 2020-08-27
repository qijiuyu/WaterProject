package com.water.project.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.water.project.R;

import java.util.ArrayList;
import java.util.List;

public class SelectRoadView extends Dialog implements View.OnClickListener {

    private Activity context;
    private TextView textView;
    private CycleWheelView wheel;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wheel_select);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.width = context.getResources().getDisplayMetrics().widthPixels; // 宽度
        initView();
        initListener();
    }

    public SelectRoadView(Activity context, TextView textView) {
        super(context, R.style.ActionSheetDialogStyle);
        this.context = context;
        this.textView=textView;
    }

    private void initView() {
        wheel=findViewById(R.id.wheel);
        List<String> list=new ArrayList<>();
        for (int i=1;i<61;i++){
             if(i<10){
                 list.add("0"+i);
             }else{
                 list.add(String.valueOf(i));
             }
        }
        wheel.setLabels(list);
        wheel.setSelection(0);
        try {
            wheel.setWheelSize(5);
        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
        wheel.setCycleEnable(false);
        wheel.setAlphaGradual(0.5f);
        wheel.setDivider(Color.parseColor("#abcdef"),1);
        wheel.setSolid(Color.WHITE,Color.WHITE);
        wheel.setLabelColor(Color.GRAY);
        wheel.setLabelSelectColor(Color.BLACK);
    }

    private void initListener() {
        findViewById(R.id.tv_cancle).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                 textView.setText(wheel.getSelectLabel());
                 break;
            case R.id.tv_cancle:
                 break;
            case R.id.rel:
                 break;
        }
        dismiss();
    }
}
