package com.test.androidutil;

import android.app.Application;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2017/12/5 0005.
 */

public class UtilApplication extends Application {
    public static UtilApplication CONTEXT;
    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics mDisplayMetrics = getApplicationContext().getResources()
                .getDisplayMetrics();
        mScreenWidth = mDisplayMetrics.widthPixels;
        mScreenHeight = mDisplayMetrics.heightPixels;
        CONTEXT = this;
    }
}
