package com.water.project.activity.aaa;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.water.project.R;

/**
 * Created by Administrator on 2019/9/24.
 */

public class AActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }
}
