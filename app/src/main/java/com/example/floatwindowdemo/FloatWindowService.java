package com.example.floatwindowdemo;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
/**
 * Created by rylene_li on 2019/1/21.
 */

/****
 #@Service
 #1. 启动时 create floating window
 #2. 收到 update message时，更新floating window的相关数据
 #3. 退出时 remove floating window
 -----------------------------
 ****/

public class FloatWindowService extends Service {

    private static String TAG = "FloatWindowService";
    private int time;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");
        Log.d(TAG, "ready to create float window");
       // MyWindowManager.initfloatWindow(getApplicationContext());
        MyWindowManager.createfloatWindow(getApplicationContext());
        mhandler.postDelayed(MyRunnable,0);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "ondestroy");
        super.onDestroy();
        mhandler.removeCallbacks(MyRunnable);
        MyWindowManager.removefloatWindow(getApplicationContext());
        MyWindowManager.removeWindow(getApplicationContext());
        Log.d(TAG, "done remove float window");
    }

    /***************定时器中更新数据**********************/

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                MyWindowManager.update((time++));
            }
        }
    };
    private Runnable MyRunnable = new Runnable() {
        @Override
        public void run() {
            MyWindowManager.update((time++));
            mhandler.postDelayed(this,1000);
        }
    };
}

