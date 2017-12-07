package com.test.androidutil.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yifan.shufa.global.Constant;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public class SPUtil {
    public final static String SETTING = "Setting";
    private static final String PREF_NAME = "config";

    private static SharedPreferences getSP(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp;
    }

    public static String getString(Context context, String key, String defaultValues) {
        return getSP(context).getString(key, defaultValues);
    }

    public static String getGrade(Context context) {
        return getString(context, "grade", null);
    }
    public static String getMaterial(Context context) {
        return getString(context, "material", null);
    }
    public static String getCardsName(Context context){
        return getSP(context).getString("cardsName",null);
    }
    public static String getCardsPassword(Context context){
        return getSP(context).getString("cardsPassword",null);
    }
    public static void putString(Context context, String key, String values) {
        getSP(context).edit().putString(key, values).commit();
    }


    public static int getInt(Context context, String key, int defaultValues) {
        return getSP(context).getInt(key, defaultValues);
    }

    public static int getGradeId(Context context) {
        return getInt(context, "grade_id", 0);
    }

    public static int getMaterialId(Context context) {
        return getInt(context, "material_id", 0);
    }

    public static int getIsPay(Context context) {
        int id = getInt(context, "grade_id", -1);
        return getInt(context, id + Constant.USERNAME, 0);
    }

    public static void putInt(Context context, String key, int time) {
        getSP(context).edit().putInt(key, time).commit();
    }

    public static void putBoolean(Context context, String key, boolean b) {
        getSP(context).edit().putBoolean(key, b).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValues) {
        return getSP(context).getBoolean(key, defaultValues);
    }

    public static void putValue(Context context, String key, int value) {
        SharedPreferences.Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putInt(key, value);
        sp.commit();
    }

    public static int getValue(Context context, String key, int defValue) {
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        int value = sp.getInt(key, defValue);
        return value;
    }



}
