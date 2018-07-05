package com.water.project.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.water.project.R;

/**
 * 首页
 * Created by Administrator on 2018/7/2 0002.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView(){
        findViewById(R.id.tv_setting).setOnClickListener(this);
        findViewById(R.id.tv_about).setOnClickListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.tv_searchOld).setOnClickListener(this);
        findViewById(R.id.tv_upload).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_setting:
                 setClass(SettingOneActivity.class);
                 break;
            case R.id.tv_search:
                 setClass(SearchDataActivity.class);
                 break;
            case R.id.tv_upload:
                 setClass(UploadActivity.class);
                 break;
            case R.id.tv_searchOld:
                 setClass(SearchOldActivity.class);
                 break;
            case R.id.tv_about:
                setClass(AboutActivity.class);
                 break;
            default:
                break;
        }

    }
}
