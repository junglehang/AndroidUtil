package com.test.androidutil.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;


/**
 * author：Lynn on 2016/11/1 14:36
 * <p>
 * E-mail：lynn_47253@sina.com
 *
 * function:
 * 1、点击嵌套在ScrollView内的EditText 外部把软键盘收起
 * 2、ScrollView能正常滑动
 * 步骤：
 * 1、重写ScrollView 的OnTouchEvent()方法
 * 2、判断Action_Down 和Action_UP 的Y值如果相等则把软键盘收起
 */


public class OnTouchScrollView extends ScrollView {
    /**
     * 手指点击屏幕时Y坐标
     */
    float downY;
    /**
     * 手指抬起离开屏幕时Y坐标
     */
    float upY;
    public OnTouchScrollView(Context context) {
        super(context);
    }

    public OnTouchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnTouchScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                upY = ev.getY();
                //  点击和抬起相差200个像素以内则认为是相等收起软键盘
                if(Math.abs((downY-upY)) <= 200){
                    Activity activity = (Activity) getContext();
                                     //收键盘
                    InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE); //初始化InputMethodManager类
                    if (activity.getCurrentFocus() != null
                            && activity.getCurrentFocus().getWindowToken() != null) {
                        manager.hideSoftInputFromWindow(activity.getCurrentFocus()
                                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
}
