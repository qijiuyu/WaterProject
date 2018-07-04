package com.water.project.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.water.project.R;

/**
 * 动画类
 * Created by Administrator on 2017/11/3 0003.
 */

public class AnimUtils {

    public static boolean is=false;
    public static void start(final Context context, final View v,final View parent, final boolean b){
        if(!b && !is){
            return;
        }
        TranslateAnimation translateAnimation=null;
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);

        if(b){
            translateAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.main_bottom_right);
        }else{
            translateAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.main_bottom_left);
        }
        v.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                //清除动画
                v.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                if(b){
                    is=true;
                    params.leftMargin = params.leftMargin + (parent.getWidth()-v.getWidth());
                }else{
                    is=false;
                    params.leftMargin = params.leftMargin - (parent.getWidth()-v.getWidth());
                }
                v.setLayoutParams(params);
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
