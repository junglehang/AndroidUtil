package com.test.androidutil.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/6/6 0006.
 */

public class FontUtils {
    /**
     * 设置字体
     *
     * @param context
     * @param textViews
     */
    public static void setTypeFace(Context context, TextView... textViews) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/caiti.ttf");
        for (TextView textView : textViews) {
            textView.setTypeface(typeface);
        }
    }

}
