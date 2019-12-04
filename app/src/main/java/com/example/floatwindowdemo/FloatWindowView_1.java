package com.example.floatwindowdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

//import com.example.Activity.ExamActivity;

import java.util.ArrayList;
import java.util.List;

public class FloatWindowView_1 extends RelativeLayout {
    private static String TAG = "FloatWindowView_1";
    public static int viewWidth;
    public static int viewHeight;
    private WindowManager windowManager;                //用于更新悬浮窗1的位置
    private WindowManager.LayoutParams mParams;         //悬浮窗1的参数
    private ImageButton enter_btn,exit_btn;             //这里是弹出带两个button内容的悬浮窗

    private List<ImageButton> buttons = new ArrayList<>();

    public FloatWindowView_1(final Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_1, this);
        View view = findViewById(R.id.view_floatwindow_1);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        enter_btn = (ImageButton) findViewById(R.id.enter_test);
        exit_btn = (ImageButton) findViewById(R.id.exit_test);
        buttons.add(exit_btn);
        buttons.add(enter_btn);
        showOpenAnim(20); //添加弹出动画
        /*
         *button点击事件可以修改为其他逻辑
         */
        enter_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，停止Service，移除所有悬浮窗，
                Log.i(TAG, "enter_btn.setOnClickListener");
                Intent intent = new Intent(getContext(), FloatWindowService.class);
                context.stopService(intent);
                Log.i(TAG, "ready go back to enter_btn.setOnClickListener");
            }
        });
        exit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                System.exit(0);
            }
        });
    }

    public void setBigParams(WindowManager.LayoutParams params) {
        mParams = params;
    }
    public void updateViewPosition(int x,int y) {
        if (mParams==null)
        {
            Log.i(TAG, "updateViewbigPosition=====-----------fail");
        }
        mParams.x = x;
        mParams.y = y;
        windowManager.updateViewLayout(this, mParams);
    }
    //打开悬浮窗1的属性动画， dp为半径长度
    private void showOpenAnim(int dp) {
        enter_btn.setVisibility(View.VISIBLE);
        exit_btn.setVisibility(View.VISIBLE);
        //for循环来开始小图标的出现动画
        for (int i = 0; i < buttons.size(); i++) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(buttons.get(i),"translationX",(float)0,dpTopx.dip2px(getContext(),10*(i+1)))
                    , ObjectAnimator.ofFloat(buttons.get(i), "alpha", 0, 1).setDuration(2000)
            );
            set.setInterpolator(new BounceInterpolator());
            set.setDuration(500).setStartDelay(100);
            set.start();
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }
}
