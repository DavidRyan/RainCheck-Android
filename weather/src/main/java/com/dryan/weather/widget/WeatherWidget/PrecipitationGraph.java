package com.dryan.weather.widget.WeatherWidget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.CycleInterpolator;


import com.dryan.weather.WeatherApp;

import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 3/3/14.
 */
public class PrecipitationGraph extends View {

    ArrayList<Point> mPoints = new ArrayList<Point>();
    Paint mPaint = new Paint();
    Path mPath;
    final static int BASE_BAR = 200;
    private boolean zero = true;

    public PrecipitationGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PrecipitationGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrecipitationGraph(Context context) {
        super(context);
    }

    public void setPoints(ArrayList<Point> aPoints) {
        mPoints = aPoints;
        zero = true;
        for (Point p : mPoints) {
            if (p.y != 0.0) {
                zero = false;
            }
            p.x = convertXtoPixel(p.x);
            p.y = convertYtoPixel(p.y);
            ObjectAnimator animator = ObjectAnimator.ofInt(p, "y", (int)(p.y), getRandomY(p.y)).setDuration(5000);
            animator.setInterpolator(new CycleInterpolator(getRandomY(4)));
            animator.setRepeatCount(10000);
            animator.start();
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
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

        mPaint.setColor(0x360000FF);
        mPaint.setStyle(Paint.Style.FILL);
        drawRainPath(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        drawRainPath(canvas);
        drawLabels(canvas);
        invalidate();

    }

    public void drawLabels(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(WeatherApp.getRobotoBold(getContext()));
        canvas.drawText("30 min", ((getWidth() / 2) - 50), BASE_BAR + 40, mPaint);
        canvas.drawText("10 min", ((getWidth() / 6) - 50), BASE_BAR + 40, mPaint);
        canvas.drawText("50 min", (((getWidth() / 6)*5) - 60), BASE_BAR + 40, mPaint);
        if (zero) {
            mPaint.setTypeface(WeatherApp.getRoboto(getContext()));
            mPaint.setTextSize(30);
            canvas.drawText("ALL CLEAR", ((getWidth() / 2) - 70), BASE_BAR/2, mPaint);
        }
    }

    public void drawRainPath(Canvas canvas) {
        mPath = new Path();
        if (mPoints.size() > 0) mPath.moveTo(0, (int)(mPoints.get(0).y));
        Point p;
        double lastX = 0, lastY = 0;
        for (int i = 0 ; i < mPoints.size(); i++) {
            p = mPoints.get(i);
                if (i == 0) {
                    mPath.lineTo((int) p.x, (int) p.y);
                } else if (i == mPoints.size()) {
                    mPath.lineTo((int) (p.x), (int) (p.y));
                } else {
                    mPath.quadTo((int) lastX, (int) lastY, (int) (p.x + lastX) / 2, (int) (p.y + lastY) / 2);
                }
                lastX = p.x;
                lastY = p.y;


        }
        mPath.lineTo(getWidth(), BASE_BAR);
        mPath.lineTo(0, BASE_BAR);
        mPath.close();
        if (!zero) {
            canvas.drawPath(mPath, mPaint);
        }

    }

    private float convertXtoPixel(double x) {
        int offset = getWidth()/60*2;
        return (int) x*offset;
    }


    private float convertYtoPixel(double y) {
     //   if (y == 0) return BASE_BAR;
        int result = (int) ((BASE_BAR) - ((y*10000) * 0.0666));
        Timber.d("result " + result);
        if (result > 200) result = 200;
        if (result < 1) result = 5;
        return result;
    }

    public int getRandomY(double y) {
      //  if (y == 0) return 0;
        if (y == 200) return 199;
        double start = 1;
        double end = 7;
        double random = new Random().nextDouble();
        double result = start + (random * (end - start));
        int newValue = (int) (y + result);
        if (newValue > BASE_BAR) return BASE_BAR;
        return newValue;
    }

    public static class Point {
        public double x;
        public double y;

        public void setY(int aY) {
            y = aY;
        }
    }
}

