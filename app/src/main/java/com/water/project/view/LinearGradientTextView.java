package com.water.project.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.water.project.R;


/**
 * Created by Fly on 2017/6/6.
 */
public class LinearGradientTextView extends AppCompatTextView {

    private TextPaint paint;
    private LinearGradient linearGradient;
    private Matrix matrix;
    private float translateX;
    private float deltaX = 20;

    private int showTime;//显示的时间
    private int lineNumber;//行数
    private int showStyle;
    public static final int UNIDIRECTION = 0;
    public static final int TWOWAY = 1;
    private int color;

    public LinearGradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinearGradientTextView4);
        showTime = typedArray.getInteger(R.styleable.LinearGradientTextView4_showTime, 40);
        lineNumber = typedArray.getInteger(R.styleable.LinearGradientTextView4_lineNumber, 1);
        showStyle = typedArray.getInt(R.styleable.LinearGradientTextView4_showStyle, UNIDIRECTION);
        color = typedArray.getColor(R.styleable.LinearGradientTextView4_textColor, Color.BLUE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint = getPaint();
        String text = getText().toString();
        float textWidth = paint.measureText(text);
        //GradientSize=三个文字的大小
        int gradientSize = (int) (3 * textWidth / text.length());
        //边缘融合
        linearGradient = new LinearGradient(-gradientSize, 0, gradientSize, 0, new int[]{color - 0xAF000000, color, color - 0xAF000000},
                new float[]{0, 0.5f, 1}, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        matrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float textWidth = (getPaint().measureText(getText().toString())) / lineNumber;
        translateX += deltaX;
        switch (showStyle) {
            case UNIDIRECTION:
                //单向闪动
                if (translateX > textWidth + 1 || translateX < 1) {
                    translateX = 0;
                    translateX += deltaX;
                }
                break;
            case TWOWAY:
                //来回闪动
                if (translateX > textWidth + 1 || translateX < 1) {
                    deltaX = -deltaX;
                }
                break;
        }

        matrix.setTranslate(translateX, 0);
        linearGradient.setLocalMatrix(matrix);
//        if (lineNumber > 1) {
//            postInvalidateDelayed(showTime * lineNumber);
//        } else {
//            postInvalidateDelayed(showTime);
//        }
        postInvalidateDelayed(showTime * lineNumber);
//        postInvalidate();
    }
}

