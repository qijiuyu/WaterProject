package com.water.project.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import com.water.project.R;
import com.water.project.view.DialogView;

public class WelcomeActivity extends BaseActivity {

    private RelativeLayout relativeLayout;
    private Animation myAnimation_Alpha;
    private DialogView dialogView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_welcome);
        initView();
        initAnim();

    }

    private void initView() {
        relativeLayout=(RelativeLayout)findViewById(R.id.lin_start);
    }

    private void initAnim() {
        myAnimation_Alpha = new AlphaAnimation(0.1f, 1.0f);
        myAnimation_Alpha.setDuration(3000);
        myAnimation_Alpha.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //渐变动画结束后，执行此方法，跳转到主界面/引导页
                isSupportBle();//判断设备是否支持蓝牙4.0
            }
        });
        relativeLayout.setAnimation(myAnimation_Alpha);
        myAnimation_Alpha.start();
    }


    /**
     * 判断设备是否支持蓝牙4.0
     */
    private void isSupportBle(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            dialogView = new DialogView(mContext, "该设备不支持ble蓝牙!","知道了", null, new View.OnClickListener() {
                public void onClick(View v) {
                    dialogView.dismiss();
                    finish();
                }
            }, null);
            dialogView.show();
        }else{
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
    }

}
