package com.eecs.screencapture;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;


public class FloatingService extends Service{

    private final int                   TENSION = 400;
    private final int                   FRICTION = 10;

    private WindowManager               windowManager;
    private ImageView                   floatingHead;
    private WindowManager.LayoutParams  floatingHeadParams;
    private Display                     display;
    private Point                       screenSize = new Point(0,0);

    private ImageView                   dismissBin;
    private WindowManager.LayoutParams  dismissBinParams;

    private SpringSystem                springSystem;
    private Spring                      floatingHeadSpring;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager                   = (WindowManager) getSystemService(WINDOW_SERVICE);
        display                         = windowManager.getDefaultDisplay();
        display.getSize(screenSize);

        floatingHead                    = new ImageView(this);
        floatingHead.setBackgroundResource(R.mipmap.floating_head);

        floatingHeadParams              = new WindowManager.LayoutParams(100, 100, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        floatingHeadParams.gravity      = Gravity.TOP | Gravity.LEFT;
        floatingHeadParams.x            = 0;
        floatingHeadParams.y            = 100;

        springSystem                    = SpringSystem.create();
        floatingHeadSpring              = springSystem.createSpring();

        floatingHeadSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                floatingHead.setScaleX(scale);
                floatingHead.setScaleY(scale);
            }
        });

        SpringConfig floatingHeadConfig = new SpringConfig(TENSION, FRICTION);
        floatingHeadSpring.setSpringConfig(floatingHeadConfig);

        dismissBin                      = new ImageView(this);
        dismissBin.setBackgroundResource(R.mipmap.dismiss_button);

        dismissBinParams                = new WindowManager.LayoutParams(100, 100, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        dismissBinParams.gravity        = Gravity.TOP | Gravity.LEFT;
        dismissBinParams.x              = screenSize.x / 2 - 50;
        dismissBinParams.y              = screenSize.y - 100;


        floatingHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = floatingHeadParams.x;
                        initialY = floatingHeadParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        floatingHeadSpring.setEndValue(1f);
                        windowManager.addView(dismissBin, dismissBinParams);
                        return true;
                    case MotionEvent.ACTION_UP:
                        windowManager.removeView(dismissBin);

                        floatingHeadSpring.setEndValue(0f);
                        if(distanceBetweenHeads() <= 80) {
                            stopSelf();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        floatingHeadParams.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        floatingHeadParams.y = initialY
                                + (int) (event.getRawY() - initialTouchY);

                        windowManager.updateViewLayout(floatingHead, floatingHeadParams);
                        return true;
                }
                return false;
            }
        });
        windowManager.addView(floatingHead, floatingHeadParams);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingHead != null)
            windowManager.removeView(floatingHead);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public int distanceBetweenHeads() {
        int distance = (int) Math.sqrt((dismissBinParams.x - floatingHeadParams.x)*(dismissBinParams.x - floatingHeadParams.x) +
                (dismissBinParams.y - floatingHeadParams.y)*(dismissBinParams.y - floatingHeadParams.y));

        return distance;
    }
}
