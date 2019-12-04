package com.example.floatwindowdemo;


import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.floatwindowdemo.FloatWindowView_1;
import com.example.floatwindowdemo.FloatWindowView;

import java.lang.reflect.Field;

public class MyWindowManager {
/*
 *悬浮窗指最初显示的悬浮窗，悬浮窗1指点击后弹出的小的悬浮窗
 */
    private static String TAG = "MyWindowManager";
    private static WindowManager mWindowManager;                //floatWindow管理
    private static FloatWindowView floatWindow;                 //floatWindowView的实例
    private static FloatWindowView_1 floatWindowView_1;         //floatWindowView_1的实例
    public static WindowManager.LayoutParams floatWindowParams;//floatWindowView的参数
    public static WindowManager.LayoutParams floatWindowParams_1;//floatWindowView_1的参数

    public static int smallWindow_w;    //小窗口宽高
    public static int smallWindow_h;
    public static int statusBarHeight;                 //记录系统状态栏的高度

    /**
     * 创建悬浮窗
     * 必须为应用程序的Context.
     */

    public static void createfloatWindow(Context context) {

        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (floatWindow == null) {
            floatWindow = new FloatWindowView(context);
            if (floatWindowParams == null) {
                Log.d(TAG, "set floatWindowparams");
                floatWindowParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= 26) { //windowManager的悬浮窗口的不同Android版本适配
                    floatWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                }
                else {
                    floatWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                floatWindowParams.format = PixelFormat.RGBA_8888;
                floatWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                floatWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                floatWindowParams.width = FloatWindowView.viewWidth;//窗口大小
                floatWindowParams.height = FloatWindowView.viewHeight;
                smallWindow_w = floatWindowParams.width;
                smallWindow_h = floatWindowParams.height;
                floatWindowParams.x =screenWidth/ 2 - floatWindowParams.width/2;//窗口初始位置
                floatWindowParams.y =screenHeight * 3 / 5 - floatWindowParams.height/2;
            }
            /**将悬浮窗参数floatWindowParams传到FloatWindowView以使用（计算移动位置）*/
            floatWindow.setParams(floatWindowParams);
            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(floatWindow, floatWindowParams);//WindowManager的addView方法有两个参数，一个是需要加入的控件对象，另一个参数是WindowManager.LayoutParam对象
            Log.d(TAG, "add windowview");
        }
    }

    /**
     * 将悬浮窗从屏幕上移除。
     *
     * @param context
     * 必须为应用程序的Context.
     */

    public static void removefloatWindow(Context context) {
        Log.d(TAG, "enter remove float window");
        if (floatWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(floatWindow);
            floatWindow = null;
            floatWindowParams = null;
        }
    }
    /**
     * 更新悬浮窗内TextView信息
     *
     */

    public static void update(int time) {
        floatWindow.setText(time);
    }

    /**
     * 创建一个悬浮窗1
     *
     * @param context
     * 必须为应用程序的Context.
     */
    public static FloatWindowView_1 createWindow(Context context, float bigWindow_x, float bigWindow_y, float xInScreen_1) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (floatWindowView_1 == null) {
            floatWindowView_1 = new FloatWindowView_1(context);
            if (floatWindowParams_1 == null) {
                floatWindowParams_1 = new WindowManager.LayoutParams();
                floatWindowParams_1.width = FloatWindowView_1.viewWidth;
                floatWindowParams_1.height = FloatWindowView_1.viewHeight;
                //创建悬浮窗1时设置初始位置计算参考FloatWindowView中注释，左侧计算，右侧计算
                boolean inRight = false;
                if (windowManager.getDefaultDisplay().getWidth()-floatWindowParams.width-floatWindowParams.x>floatWindowParams_1.width) {
                    floatWindowParams_1.x = (int)bigWindow_x + smallWindow_w;
                }
                else
                {
                    floatWindowParams_1.x = (int)bigWindow_x - floatWindowParams_1.width;
                }
                floatWindowParams_1.y = (int)bigWindow_y +smallWindow_h-floatWindowParams_1.height;
                floatWindowParams_1.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                floatWindowParams_1.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                floatWindowParams_1.format = PixelFormat.RGBA_8888;
                floatWindowParams_1.gravity = Gravity.LEFT | Gravity.TOP;

            }
            floatWindowView_1.setBigParams(floatWindowParams_1);
            windowManager.addView(floatWindowView_1, floatWindowParams_1);
        }
        return floatWindowView_1;
    }
    /**
     * 将大悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeWindow(Context context) {
        Log.d(TAG, "enter remove float window 1");
        if (floatWindowView_1 != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(floatWindowView_1);
            floatWindowView_1 = null;
            floatWindowParams_1 = null;
        }
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context
     * 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }



    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
