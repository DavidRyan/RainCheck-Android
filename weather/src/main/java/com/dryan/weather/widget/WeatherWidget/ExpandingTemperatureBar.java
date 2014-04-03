package com.dryan.weather.widget.WeatherWidget;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Cold-One-City-USA on 2/27/14.
 */
public class ExpandingTemperatureBar extends View {

    Paint paint = new Paint();
    Paint text = new Paint();
    Rect mRect = new Rect();
    Rect rect1 = new Rect();
    Rect rect2 = new Rect();
    ArgbEvaluator mEval = new ArgbEvaluator();

    private int mMinTemp;
    private int mMaxTemp;
    private int mLow;
    private int mHigh;

    private int mBarTop;
    private int mBarBottom;
    private int mTextHeight;
    private float mEndPercent;
    private float mStartPercent;
    private int mStartGradient;
    private int mEndGradient;
    private int[] mColors = {0,0};
    private int mBarColor = 0;
    RectF f = new RectF();

    public ExpandingTemperatureBar(Context context) {
        super(context);
    }

    public ExpandingTemperatureBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandingTemperatureBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxTemp(int aTemp) {
        mMaxTemp = aTemp+10;
    }

    public void setMinTemp(int aTemp) {
        mMinTemp = aTemp-10;
    }

    public void setHigh(int high) {
        mHigh = high;
    }

    @Override
    protected void onAttachedToWindow () {
        super.onAttachedToWindow();
    }

    public void begin() {
        int range = mMaxTemp - mMinTemp;
        if (range == 0) return;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int offset = width / range;


        paint.setAntiAlias(true);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.FILL);

        text.setAntiAlias(true);
        text.setStrokeWidth(10);
        text.setColor(mBarColor);
        text.setStyle(Paint.Style.FILL);
        text.setTextSize(28);

        int startXY = (mLow - mMinTemp) * offset;
        int endXY = (mHigh * offset);
        int startingPoint = startXY + ((endXY - startXY) / 2);

        mStartPercent = (float) startXY / (width-20);
        mEndPercent = (float) endXY / (width-20);

        mBarBottom = (height / 4) * 4;
        mBarTop = (height / 4)* 2;
        mTextHeight = ((mBarTop - mBarBottom) / 2) + mBarBottom + ((mBarTop - mBarBottom / 4) * 2);

        mRect.set(0, mBarTop, ((mHigh/4)*3)*offset, mBarBottom);
        rect1.set(0, mBarTop, endXY, mBarBottom);
        rect2 = mRect;
        ObjectAnimator.ofObject(ExpandingTemperatureBar.this, "bounds", new RectEvaluator(), rect2, rect1).setDuration(1000).start();

    }

    public void setColor(int aColor) {
        paint.setColor(aColor);
    }

    public void setBounds(Rect aRect) {
        mRect = aRect;
        mStartGradient = (Integer) mEval.evaluate(mStartPercent, 0xFF0000CC, mBarColor);
        mEndGradient = (Integer) mEval.evaluate(mEndPercent, 0xFF0000CC, mBarColor);
        mColors[0] = mStartGradient;
        mColors[1] = mEndGradient;
        postInvalidate();
    }

    @Override
    public boolean isDrawingCacheEnabled() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        begin();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        f.set(mRect);
        canvas.drawRoundRect(f, 5, 5, paint);
        text.setColor(Color.BLACK);
        canvas.drawText("" + mHigh, mRect.right+5, getHeight()-5, text);
        super.onDraw(canvas);
    }
}
