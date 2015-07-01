package com.eecs.screencapture;


import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


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


        floatingHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private int finalTouchX;
            private int differenceX;
            private int differenceY;
            private int initialXSpecial;
            private int initialYSpecial;
            private int finalTouchY;
            private boolean flagY;
            private boolean flagX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flagX=true;
                flagY=true;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = layoutParams.x;
                        initialY = layoutParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        initialXSpecial=(int)event.getX();
                        initialYSpecial=(int)event.getY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        finalTouchX = (int) event.getX();
                        finalTouchY = (int) event.getY();
                        differenceX = finalTouchX - initialXSpecial;
                        differenceY = finalTouchY - initialYSpecial;
                        if(differenceY==0)
                        {
                            flagY=false;
                        }
                        if(differenceX==0)
                        {
                            flagX=false;
                        }
                        if (flagY||flagX) {
                            return true;
                        } else {
                            Bitmap bitmap = takeScreenshot();
                            saveBitmap(bitmap);
                        }
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
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

