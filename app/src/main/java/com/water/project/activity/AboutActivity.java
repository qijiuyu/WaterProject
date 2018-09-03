package com.water.project.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.water.project.R;
import com.water.project.utils.StatusBarUtils;
import com.water.project.utils.SystemBarTintManager;

/**
 * 关于我们
 * Created by Administrator on 2018/7/4 0004.
 */

public class AboutActivity extends BaseActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_about);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_1fc37f);
        initView();
    }

    private void initView(){
        TextView textView=(TextView)findViewById(R.id.tv_head);
        textView.setText("关于我们");
        findViewById(R.id.tv_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });

    }
}
