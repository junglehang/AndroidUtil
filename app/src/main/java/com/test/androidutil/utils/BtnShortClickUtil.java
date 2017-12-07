package com.test.androidutil.utils;

/**
 * @author 作者 E-mail:Lynn-2963885227@qq.com
 * @version 创建时间：2016年5月7日 上午11:59:37 类说明
 */
public class BtnShortClickUtil {
	private static long lastClickTime;

	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 500) {
			lastClickTime = time;
			System.out.println("点击过快"+(time-lastClickTime));
			return true;
		}
		lastClickTime = time;
		return false;
	}

}
