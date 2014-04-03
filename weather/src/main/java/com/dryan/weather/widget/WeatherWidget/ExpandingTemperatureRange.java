package com.dryan.weather.widget.WeatherWidget;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Cold-One-City-USA on 2/27/14.
 */
public class ExpandingTemperatureRange extends View {

    Paint paint = new Paint();
    Paint text = new Paint();
    Rect mRect = new Rect();
    Rect rect1 = new Rect();
    Rect rect2 = new Rect();
    RectF mRectF = new RectF();
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

    public ExpandingTemperatureRange(Context context) {
        super(context);
    }

    public ExpandingTemperatureRange(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandingTemperatureRange(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setBackgroundColor(Color.BLUE);
    }

    public void setMaxTemp(int aTemp) {
        mMaxTemp = aTemp+10;
    }

    public void setMinTemp(int aTemp) {
        mMinTemp = aTemp-10;
    }

    public void setHighAndLow(int high, int low) {
        mHigh = high;
        mLow = low;
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
        paint.setColor(Color.BLACK);

        text.setAntiAlias(true);
        text.setStrokeWidth(10);
        text.setStyle(Paint.Style.FILL);
        text.setColor(Color.BLACK);
        text.setTextSize(40);

        int startXY = (mLow - mMinTemp) * offset;
        int endXY = width - ((mMaxTemp - mHigh) * offset);
        int startingPoint = startXY + ((endXY - startXY) / 2);

        mStartPercent = (float) startXY / (width-20);
        mEndPercent = (float) endXY / (width-20);

        mBarBottom = (height / 4) * 3;
        mBarTop = (height / 4);
        mTextHeight = ((mBarTop - mBarBottom) / 2) + mBarBottom + ((mBarTop - mBarBottom / 4) * 2);

        mRect.set(startingPoint, mBarTop, startingPoint, mBarBottom);
        rect1.set(startXY, mBarTop, endXY, mBarBottom);
        rect2 = mRect;
        ObjectAnimator.ofObject(ExpandingTemperatureRange.this, "bounds", new RectEvaluator(), rect2, rect1).setDuration(1500).start();
    }

    public void setBounds(Rect aRect) {
        mRect = aRect;
        mStartGradient = (Integer) mEval.evaluate(mStartPercent, 0xFF0000CC, 0xFF99CCFF);
        mEndGradient = (Integer) mEval.evaluate(mEndPercent, 0xFF0000CC, 0xFF99CCFF);
        mColors[0] = mStartGradient;
        mColors[1] = mEndGradient;
        invalidate();
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
        mRectF.set(mRect);
        paint.setShader(new LinearGradient(mRectF.left, mRectF.top, mRectF.right, mRectF.bottom, mColors, null, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(mRectF, 20, 20, paint);
        canvas.drawText("" + mHigh, mRect.right + 5, mTextHeight, text);
        canvas.drawText(""+mLow,mRect.left-50, mTextHeight, text);
        super.onDraw(canvas);
    }
}
