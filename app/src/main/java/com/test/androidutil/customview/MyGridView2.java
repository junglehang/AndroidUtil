package com.test.androidutil.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2017/9/11 0011.
 */

public class MyGridView2 extends GridView {
    public MyGridView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView2(Context context) {
        super(context);
    }

    public MyGridView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
