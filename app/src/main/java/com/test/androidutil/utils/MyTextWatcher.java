package com.test.androidutil.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.yifan.shufa.activity.PayActivity;

/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class MyTextWatcher implements TextWatcher {
    private int maxLength = 10;
    private EditText editText;
    private Context mContext;

    int cursor = 0;// 用来记录输入字符的时候光标的位置
    int before_length;// 用来标注输入某一内容之前的编辑框中的内容的长度

    public MyTextWatcher(EditText et_yaoqingma, PayActivity payActivity, int max) {
        this.editText = et_yaoqingma;
        this.mContext = payActivity;
        this.maxLength = max;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        before_length = charSequence.length();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int befor, int count) {
        cursor = start;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (before_length>=6){

        }
    }
}
