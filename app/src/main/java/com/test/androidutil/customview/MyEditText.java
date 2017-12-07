package com.test.androidutil.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;


/**
 * author：Lynn on 2016/10/31 11:39
 * <p>
 * E-mail：lynn_47253@sina.com
 */


public class MyEditText extends EditText {
    private String TAG = "MyEditText";
    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private BackClickListener mlistener;
    public  interface BackClickListener{
        void onPhysicsBackClick();
    }
    public void setOnPhysicsBackClick(BackClickListener listener){
        mlistener = listener;
    }
    //  监听物理返回键
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mlistener != null){
                mlistener.onPhysicsBackClick();
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
