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
    private WindowManager.LayoutParams  floatingHeadParams;
    private Display                     display;
    private Point                       screenSize = new Point(0,0);

    private ImageView                   dismissBin;
    private WindowManager.LayoutParams  dismissBinParams;


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


        dismissBin                      = new ImageView(this);
        dismissBin.setBackgroundResource(R.mipmap.dismiss_button);

        dismissBinParams                = new WindowManager.LayoutParams(100, 100, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        dismissBinParams.gravity        = Gravity.TOP | Gravity.LEFT;
        dismissBinParams.x              = screenSize.x / 2 - 50;
        dismissBinParams.y              = screenSize.y + 200;


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
                        return true;
                    case MotionEvent.ACTION_UP:

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
        windowManager.addView(dismissBin, dismissBinParams);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingHead != null)
            windowManager.removeView(floatingHead);
            windowManager.removeView(dismissBin);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
