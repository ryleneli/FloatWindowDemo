package com.example.floatwindowdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by rylene_li on 2019/1/21.
 */

public class FloatWindowView extends RelativeLayout implements View.OnTouchListener{

    private static String TAG = "FloatWindowView";

    /**将布局floatwindow.xml中设置的相关参数给悬浮窗以设置其宽高*/
    public static int viewWidth;                        //悬浮窗的宽度
    public static int viewHeight;                       //记录悬浮窗的高度


    private WindowManager windowManager;                //用于更新悬浮窗的位置
    private WindowManager.LayoutParams mParams;         //悬浮窗的参数
    private View view;
    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    private TextView testTimer;
    private boolean isMenuOpen = false;
    boolean inRight = true;
   //private RelativeLayout menu;
    private ImageView pangxie,pp_1,pp_2;
    private  FloatWindowView_1 floatWindowView_1;


    public FloatWindowView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window, this);
        view = findViewById(R.id.view_floatwindow);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        testTimer = (TextView) findViewById(R.id.time_floatwindow);
        pangxie = (ImageView) findViewById(R.id.pangxie_floatwindow);
        pp_1 = (ImageView) findViewById(R.id.pp1);
        pp_2 = (ImageView) findViewById(R.id.pp2);
        animOfpp();
        pangxie.setOnTouchListener(this);

    }

    /**如果发现用户触发了ACTION_DOWN事件，会记录按下时的坐标等数据。
     * 如果发现用户触发了ACTION_MOVE事件，则根据当前移动的坐标更新悬浮窗在屏幕中的位置。
     **/
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.i(TAG, "lrl enter onTouchEvent=====");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - MyWindowManager.getStatusBarHeight(getContext());
                xInScreen = event.getRawX();
                Log.i(TAG, "xDownInScreen is     ====="+xInScreen);
                yInScreen = event.getRawY() - MyWindowManager.getStatusBarHeight(getContext());
                Log.i(TAG, "yDownInScreen is     *****"+yInScreen);
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() -MyWindowManager.getStatusBarHeight(getContext());
                // 手指移动的时候更新悬浮窗的位置
                // 移动过程中 悬浮窗1的左右位置依然可以动态变化
                updateViewPosition();
                if (isMenuOpen){
                    if (windowManager.getDefaultDisplay().getWidth()-view.getLayoutParams().width-mParams.x>floatWindowView_1.getWidth())
                        inRight = true;
                    else inRight = false;
                }
                //左右显示，Y不变，主要是X的值（画图更好理解）
                // 右：mParams.x+MyWindowManager.smallWindow_w,以悬浮窗为参考，= 悬浮窗的X值+悬浮窗宽度
                // 左：mParams.x-floatWindowView_1.getWidth()，以悬浮窗为参考，= 悬浮窗的X值-悬浮窗1宽度
                if (isMenuOpen ) {
                    if (inRight)
                        floatWindowView_1.updateViewPosition(mParams.x+MyWindowManager.smallWindow_w,mParams.y+MyWindowManager.smallWindow_h-MyWindowManager.floatWindowParams_1.height);
                    else
                        floatWindowView_1.updateViewPosition(mParams.x-floatWindowView_1.getWidth(),mParams.y+MyWindowManager.smallWindow_h-MyWindowManager.floatWindowParams_1.height);
                }
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                // 单击打开 or 关闭悬浮窗1
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    if (!isMenuOpen) {
                        floatWindowView_1 = MyWindowManager.createWindow(getContext(),mParams.x,mParams.y,xInScreen);
                        isMenuOpen = true;
                    }else {
                        MyWindowManager.removeWindow(getContext());
                        isMenuOpen = false;
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将MyWindowManager中悬浮窗window的参数传入，用于更新悬浮窗的位置。
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新悬浮窗在屏幕中的位置，需限制边界,此处计算和layout中的view大小属性相关，若修改的话，需要有针对性
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView-dip2px(42));
        if (mParams.x <= 0)
            mParams.x =0;
        if (mParams.x >= windowManager.getDefaultDisplay().getWidth()-viewWidth)
            mParams.x = windowManager.getDefaultDisplay().getWidth()-viewWidth;
        if (mParams.y <= 0)
            mParams.y =0;
        if (mParams.y >= windowManager.getDefaultDisplay().getHeight()-viewHeight)
            mParams.y = windowManager.getDefaultDisplay().getHeight()-viewHeight;//限制边界
        windowManager.updateViewLayout(this, mParams);
    }
    /*
     *设置悬浮窗text
     */
    public void setText(int time) {
        String hh = new DecimalFormat("00").format(time / 3600);
        String mm = new DecimalFormat("00").format(time % 3600 / 60);
        String ss = new DecimalFormat("00").format(time % 60);
        testTimer.setText(hh + ":" + mm + ":" + ss);
    }

    /*
     *设置螃蟹吐泡泡动画（可以删除）
     */
    private void animOfpp()
    {
        pp_1.setVisibility(View.VISIBLE);
        pp_2.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        AnimatorSet set_puple1 = new AnimatorSet();
        AnimatorSet set_puple2 = new AnimatorSet();
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(pp_1, "alpha",0f,1.0f,0.8f,0f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(pp_1, "scaleX", 0f, 1.0f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(pp_1, "scaleY", 0f, 1.0f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(pp_1, "translationY", 0, -dip2px(40));
        ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(pp_2, "scaleX", 0f, 1.0f);
        ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(pp_2, "scaleY", 0f, 1.0f);
        ObjectAnimator objectAnimator7 = ObjectAnimator.ofFloat(pp_2, "translationY", 0, -dip2px(40));
        ObjectAnimator objectAnimator8 = ObjectAnimator.ofFloat(pp_2, "alpha",0f,1.0f,0.8f,0f);
        objectAnimator1.setRepeatCount(-1);
        objectAnimator2.setRepeatCount(-1);
        objectAnimator3.setRepeatCount(-1);
        objectAnimator4.setRepeatCount(-1);
        objectAnimator5.setRepeatCount(-1);
        objectAnimator6.setRepeatCount(-1);
        objectAnimator7.setRepeatCount(-1);
        objectAnimator8.setRepeatCount(-1);
        set_puple1.playTogether(objectAnimator1,objectAnimator2,objectAnimator3,objectAnimator4);
        set_puple1.setStartDelay(500);
        set_puple2.playTogether(objectAnimator5,objectAnimator6,objectAnimator7,objectAnimator8);
        set.play(set_puple1).with(set_puple2);
        set.setDuration(4000);
        set.start();
    }
    /*
     *dp与px换算，用于设置泡泡弹出距离
     */
    private int dip2px(int value) {
        float density = getResources().getDisplayMetrics().density;
        Log.i(TAG,"==========="+density);

        float abs = density * value+0.5f;
        Log.i(TAG,"==========="+abs);
        return (int) (density * value + 0.5f);
    }

}
