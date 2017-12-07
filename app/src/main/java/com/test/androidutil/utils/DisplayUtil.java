package com.test.androidutil.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * @author Lynn M614778
 * @version 创建时间：2016-4-3下午8:23:43 类说明
 */
public class DisplayUtil {

	private static final String TAG = "DisplayUtil";

	/**
	 * 屏幕适配方案  dip转换为px   px转换为dpi
	 * @param context
	 * @param dipValue
     * @return
     */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	public static int dip2px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	public static float px2dp(Context ctx, int px) {
		float density = ctx.getResources().getDisplayMetrics().density;
		float dp = px / density;

		return dp;
	}
	public static float px2dp(Context ctx, float px) {
		float density = ctx.getResources().getDisplayMetrics().density;
		float dp = px / density;

		return dp;
	}

	/**
	 * 获取屏幕的宽高
	 * @param context
	 * @return
     */
	public static Point getScreenMetrics(Context context){
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		Log.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
		return new Point(w_screen, w_screen);

	}
	/**
	 * 获取屏幕的宽高比
	 */
	public static float getScreenRate(Context context){
		Point P = getScreenMetrics(context);
		float H = P.y;
		float W = P.x;
		return (H/W);
	}
}
