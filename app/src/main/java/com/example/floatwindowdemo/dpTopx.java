package com.example.floatwindowdemo;

import android.content.Context;

public class dpTopx {
    public static int dip2px(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        //Log.i(TAG,"==========="+density);

        float abs = density * value+0.5f;
        //Log.i(TAG,"==========="+abs);
        return (int) (density * value + 0.5f);
    }
}
