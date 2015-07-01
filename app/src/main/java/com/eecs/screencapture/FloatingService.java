package com.eecs.screencapture;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;


public class FloatingService extends Service{

    private WindowManager               windowManager;
    private ImageView                   floatingHead;
    private WindowManager.LayoutParams  layoutParams;


    @Override
    public void onCreate() {
        super.onCreate();
        windowManager           = (WindowManager) getSystemService(WINDOW_SERVICE);

        floatingHead            = new ImageView(this);
        floatingHead.setBackgroundResource(R.mipmap.floating_head);

        layoutParams            = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,   ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity    = Gravity.TOP | Gravity.START;
        layoutParams.x          = 0;
        layoutParams.y          = 100;

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
