package com.water.project.activity.aaa;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.water.project.R;
import com.water.project.activity.BaseActivity;
import com.water.project.utils.LogUtils;

/**
 * Created by Administrator on 2019/10/17.
 */

public class Test2Activity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aaa);

        Handler handler=new Handler();
        LogUtils.e("+++++++++++++0");
        final Button btn=(Button)findViewById(R.id.btn_con);
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("+++++++++++++1");
                btn.setVisibility(View.VISIBLE);
            }
        });
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("+++++++++++++2");
            }
        });

    }
}
