package com.water.project.presenter;

import android.app.Activity;
import android.app.Dialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.water.project.R;
import com.water.project.application.MyApplication;
import com.water.project.utils.LogUtils;
import com.water.project.utils.SPUtil;
import com.water.project.utils.Util;

public class GetDataPresenterImpl {

    private Activity activity;
    private GetDataPresenter getDataPresenter;

    //数据0：表示正    1：表示负
    private int dataType=0;
    public GetDataPresenterImpl(Activity activity,GetDataPresenter getDataPresenter){
        this.activity=activity;
        this.getDataPresenter=getDataPresenter;
    }


    /**
     * 设置水温偏移量
     */
    public void setShuiWen(){
        dataType=0;
        View view=LayoutInflater.from(activity).inflate(R.layout.set_dialog,null);
        final Dialog dialog=Util.dialogPop(activity,view);
        TextView tvTitle=(TextView)view.findViewById(R.id.tv_title);
        tvTitle.setText("设置水温偏移量");
        final EditText editText=(EditText)view.findViewById(R.id.et_data);
        InputFilter[] filters = {new InputFilter.LengthFilter(5)};
        editText.setFilters(filters);
        final Spinner spinner=(Spinner)view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataType=position;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //保存
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String data=editText.getText().toString().trim();
                if(TextUtils.isEmpty(data)){
                    Toast.makeText(activity,"请输入偏移量",Toast.LENGTH_LONG).show();
                    return;
                }
                dialog.dismiss();
                String type =activity.getResources().getStringArray(R.array.spingarr1)[dataType];
                MyApplication.spUtil.addString(SPUtil.SHUI_WEN,type+data);
                getDataPresenter.updateData(1);
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    /**
     * 设置水位埋深
     */
    public void setSWMS(){
        dataType=0;
        View view=LayoutInflater.from(activity).inflate(R.layout.set_dialog,null);
        final Dialog dialog=Util.dialogPop(activity,view);
        TextView tvTitle=(TextView)view.findViewById(R.id.tv_title);
        tvTitle.setText("设置水位埋深偏移量");
        final EditText editText=(EditText)view.findViewById(R.id.et_data);
        InputFilter[] filters = {new InputFilter.LengthFilter(8)};
        editText.setFilters(filters);
        final Spinner spinner=(Spinner)view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataType=position;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //保存
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String data=editText.getText().toString().trim();
                if(TextUtils.isEmpty(data)){
                    Toast.makeText(activity,"请输入偏移量",Toast.LENGTH_LONG).show();
                    return;
                }
                dialog.dismiss();
                String type =activity.getResources().getStringArray(R.array.spingarr1)[dataType];
                MyApplication.spUtil.addString(SPUtil.SHUI_WEI_MAI_SHEN,type+data);
                getDataPresenter.updateData(2);
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    /**
     * 设置电导率
     */
    public void setDdl(){
        dataType=0;
        View view=LayoutInflater.from(activity).inflate(R.layout.set_dialog,null);
        final Dialog dialog=Util.dialogPop(activity,view);
        TextView tvTitle=(TextView)view.findViewById(R.id.tv_title);
        tvTitle.setText("设置电导率偏移量");
        final EditText editText=(EditText)view.findViewById(R.id.et_data);
        InputFilter[] filters = {new InputFilter.LengthFilter(9)};
        editText.setFilters(filters);
        final Spinner spinner=(Spinner)view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataType=position;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //保存
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String data=editText.getText().toString().trim();
                if(TextUtils.isEmpty(data)){
                    Toast.makeText(activity,"请输入偏移量",Toast.LENGTH_LONG).show();
                    return;
                }
                dialog.dismiss();
                String type =activity.getResources().getStringArray(R.array.spingarr1)[dataType];
                MyApplication.spUtil.addString(SPUtil.DIAN_DAO_LV,type+data);
                getDataPresenter.updateData(3);
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
