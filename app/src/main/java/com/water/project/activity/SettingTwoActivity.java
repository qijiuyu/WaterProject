package com.water.project.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.water.project.R;

/**
 * 设置参数2
 * Created by Administrator on 2018/7/4 0004.
 */

public class SettingTwoActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvJc,tvYq;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_setting_two);
        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        tvJc=(TextView)findViewById(R.id.tv_ast_jc);
        tvYq=(TextView)findViewById(R.id.tv_ast_yq);
        tvJc.setOnClickListener(this);
        tvYq.setOnClickListener(this);
        findViewById(R.id.tv_aso_save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_aso_jc:
                tvJc.setTextColor(getResources().getColor(android.R.color.white));
                tvJc.setBackground(getResources().getDrawable(R.drawable.bg_setting_select));
                tvYq.setTextColor(getResources().getColor(R.color.color_1fc37f));
                tvYq.setBackground(getResources().getDrawable(R.drawable.bg_setting));
                break;
            case R.id.tv_aso_yq:
                tvYq.setTextColor(getResources().getColor(android.R.color.white));
                tvYq.setBackground(getResources().getDrawable(R.drawable.bg_setting_select));
                tvJc.setTextColor(getResources().getColor(R.color.color_1fc37f));
                tvJc.setBackground(getResources().getDrawable(R.drawable.bg_setting));
                break;
            //保存
            case R.id.tv_aso_save:
                 finish();
                 break;
            case R.id.lin_back:
                finish();
                break;
            default:
                break;
        }

    }
}
