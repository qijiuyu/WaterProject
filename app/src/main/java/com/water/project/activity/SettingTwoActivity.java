package com.water.project.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.water.project.R;

/**
 * 设置参数2
 * Created by Administrator on 2018/7/4 0004.
 */

public class SettingTwoActivity extends BaseActivity implements View.OnClickListener{

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
        findViewById(R.id.tv_aso_save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
