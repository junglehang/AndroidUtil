package com.test.androidutil.utils;

import android.util.Log;

import com.yifan.shufa.global.GlobalContants;

/**
 * author：Lynn on 2016/9/18 16:19
 * <p/>
 * E-mail：lynn_47253@sina.com
 */

public class Logs {
    public static final String TAG = "Education_Android";

    public static void d(String tag, String msg) {
        if (GlobalContants.isNeedLog) {
            Log.d(TAG + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (GlobalContants.isNeedLog) {
            Log.e(TAG + tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (GlobalContants.isNeedLog) {
            Log.i(TAG + tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (GlobalContants.isNeedLog) {
            Log.v(TAG + tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (GlobalContants.isNeedLog) {
            Log.w(TAG + tag, msg);
        }
    }


    /**
     * 打印Exception捕捉后的信息
     * <p>
     * <i>描述行号以及异常信息</i>
     *
     * @param tag
     *            tag
     * @param msg
     *            描述信息
     * @param e
     *            异常
     */
    public static void exception(String tag, String msg, Exception e) {
        if (GlobalContants.isNeedLog) {
            String eMsg = "";
            int linNum = 0;
            if (e != null) {// exception可能为null
                StackTraceElement[] trace = e.getStackTrace();
                if (trace != null && trace[0] != null) {
                    linNum = trace[0].getLineNumber();
                }
                eMsg = e.getMessage();
            }
            Log.e(TAG + tag, msg + "\n行号：" + linNum + "\n异常信息：" + eMsg);
        }
    }
}
