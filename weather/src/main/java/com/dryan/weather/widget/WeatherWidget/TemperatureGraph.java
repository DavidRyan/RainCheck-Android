package com.dryan.weather.widget.WeatherWidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


import com.dryan.weather.WeatherApp;

import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 3/3/14.
 */
public class TemperatureGraph extends View {

    ArrayList<Point> mPoints = new ArrayList<Point>();
    Paint mPaint = new Paint();
    Path mPath;
    final static int BASE_BAR = 200;
    private boolean zero = true;
    int mMax = 0;
    int mMin = 200;
    int mMaxYPoint = 0;
    int mMinYPoint = 0;
    int mMaxXPoint = 0;
    int mMinXPoint = 0;
    int mOffset = 0;

    public TemperatureGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TemperatureGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TemperatureGraph(Context context) {
        super(context);
    }

    public void setPoints(ArrayList<Point> aPoints) {
        mPoints = aPoints;
        for (Point p : mPoints) {
            if (p.y > mMax) {
                mMax = (int) p.y;
                mMaxYPoint = (int) convertYtoPixel(p.y);
                mMaxXPoint = (int) convertXtoPixel(p.x);
                Timber.d("min x " + mMax + " y " + mMinYPoint + "width" + getWidth());
            }
            if (p.y < mMin) {
                mMin = (int) p.y;
                mMinYPoint = (int) convertYtoPixel(p.y);
                mMinXPoint = (int) convertXtoPixel(p.x);
            }
            p.x = convertXtoPixel(p.x);
            p.y = convertYtoPixel(p.y);
        }
        mOffset = 200 / (mMax - mMin);
        Timber.d("offset " + mOffset);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(40);

        mPath = new Path();
        mPath.moveTo(0, 10);
        mPath.lineTo(0, BASE_BAR);
        canvas.drawPath(mPath, mPaint);
        mPath.moveTo(getWidth(), 10);
        mPath.lineTo(getWidth(), BASE_BAR);
        canvas.drawPath(mPath, mPaint);

        mPath = new Path();
        mPath.moveTo(0, BASE_BAR);
        mPath.lineTo(getWidth(), BASE_BAR);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        mPath = new Path();
        if (mPoints.size() > 0) mPath.moveTo(0, (int)(mPoints.get(0).y));
        Point p = new Point();
        double lastX = 0, lastY = 0;
        for (int i = 0 ; i < mPoints.size(); i++) {
            p = mPoints.get(i);
            if (i == 0) {
                mPath.moveTo((int) p.x, (int) p.y);
            } else if (i == mPoints.size()) {
                mPath.lineTo((int) (p.x), (int) (p.y));
            } else {
                mPath.quadTo((int) lastX, (int) lastY, (int) (p.x + lastX) / 2, (int) (p.y + lastY) / 2);
            }
            lastX = p.x;
            lastY = p.y;

        }
        canvas.drawPath(mPath, mPaint);

        mPaint.setTypeface(WeatherApp.getRoboto(getContext()));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("" + mMax, 10, mMaxYPoint -6, mPaint);
        canvas.drawText("" + mMin, 10, mMinYPoint -6, mPaint);


        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(WeatherApp.getRobotoBold(getContext()));
        invalidate();

    }

    private float convertXtoPixel(double x) {
        int offset = getWidth()/48*2;
        return (int) x*offset;
    }


    private float convertYtoPixel(double y) {
        float result = (float) (BASE_BAR-20) - ((float)y*mOffset);
        return result;
    }

    public int getRandomY(double y) {
        double start = 1;
        double end = 7;
        double random = new Random().nextDouble();
        double result = start + (random * (end - start));
        return (int) (y + result);
    }

    public static class Point {
        public double x;
        public double y;

        public void setY(int aY) {
            y = aY;
        }
    }
}

