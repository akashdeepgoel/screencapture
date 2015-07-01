package com.eecs.screencapture;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;


public class FloatingService extends Service{

    private WindowManager               windowManager;
    private ImageView                   floatingHead;
    private WindowManager.LayoutParams  layoutParams;
    private Display                     display;
    private Point                       screenSize = new Point(0,0);


    @Override
    public void onCreate() {
        super.onCreate();
        windowManager           = (WindowManager) getSystemService(WINDOW_SERVICE);
        display                 = windowManager.getDefaultDisplay();
        display.getSize(screenSize);

        floatingHead            = new ImageView(this);
        floatingHead.setBackgroundResource(R.mipmap.floating_head);

        layoutParams            = new WindowManager.LayoutParams(
                (int)Math.sqrt(screenSize.x*screenSize.y/100*Math.PI), (int)Math.sqrt(screenSize.x*screenSize.y/100*Math.PI),
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity    = Gravity.TOP | Gravity.START;
        layoutParams.x          = 0;
        layoutParams.y          = 100;


        floatingHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = layoutParams.x;
                        initialY = layoutParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        layoutParams.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingHead, layoutParams);
                        return true;
                }
                return false;
            }
        });
        windowManager.addView(floatingHead, layoutParams);
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
}
