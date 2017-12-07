package com.test.androidutil.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences封装
 *
 * @author Kevin
 *
 */
public class PrefUtils {

	private static final String PREF_NAME = "config";

	public static boolean getBoolean(Context cxt, String key, boolean defaultValues) {
		SharedPreferences sp = cxt.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

		return sp.getBoolean(key, defaultValues);

	}

	public static void setBoolean(Context cxt, String key, boolean value) {
		SharedPreferences sp = cxt.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();

	}

	public static boolean getString(Context cxt, String key, boolean defaultValues) {
		SharedPreferences sp = cxt.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

		return sp.getBoolean(key, defaultValues);

	}

	public static void setSting(Context cxt, String key, boolean value) {
		SharedPreferences sp = cxt.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}
	public static String getString(Context cxt, String key, String defaultValues) {
		SharedPreferences sp = cxt.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

		return sp.getString(key, defaultValues);

	}

	public static void setString(Context cxt, String key, String value) {
		SharedPreferences sp = cxt.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

}
