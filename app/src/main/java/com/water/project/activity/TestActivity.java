package com.water.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.water.project.R;

import android.view.GestureDetector.OnGestureListener;

/**
 * 测试工具类
 */
public class TestActivity extends BaseActivity{

    private GestureDetector gestureDetector; 					//手势检测
    private OnGestureListener onSlideGestureListener = null;	//左右滑动手势检测监听器
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aaa);

        //左右滑动手势监听器
        onSlideGestureListener = new OnSlideGestureListener();
        gestureDetector = new GestureDetector(this, onSlideGestureListener);

    }


    //将touch动作事件交由手势检测监听器来处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    /*********************************************
     * 左右滑动手势监听器
     ********************************************/
    private class OnSlideGestureListener implements OnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY)
        {
            // 参数解释：
            // e1：第1个ACTION_DOWN MotionEvent
            // e2：最后一个ACTION_MOVE MotionEvent
            // velocityX：X轴上的移动速度，像素/秒
            // velocityY：Y轴上的移动速度，像素/秒
            // 触发条件 ：
            // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
            if ((e1 == null) || (e2 == null)){
                return false;
            }
            int FLING_MIN_DISTANCE = 100;
            int FLING_MIN_VELOCITY = 100;
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY)
            {
                // 向左滑动
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
//此处也可以加入对滑动速度的要求
//			             && Math.abs(velocityX) > FLING_MIN_VELOCITY
                    )
            {
                // 向右滑动
            }
            return false;
        }
    }

}
