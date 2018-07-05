package com.water.project.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.water.project.application.MyApplication;

/**
 * Created by Administrator on 2018/7/5 0005.
 */

public class FontTextView extends TextView {

    public FontTextView(Context context) {
        super(context);

    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(MyApplication.application.getAssets(),"fzybxs.TTF"));
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
