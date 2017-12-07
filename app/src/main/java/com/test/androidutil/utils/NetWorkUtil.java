package com.test.androidutil.utils;

import android.content.Context;
import android.net.ConnectivityManager;


public class NetWorkUtil {

	/**
	 * 判断手机是否处于连网状态
	 * @param context
	 * @return
	 */
	public static boolean NetWorkStatus(Context context) {

		boolean netSataus = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		cwjManager.getActiveNetworkInfo();

		if (cwjManager.getActiveNetworkInfo() != null) {
			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		/*
		if (!netSataus) {
			Builder b = new AlertDialog.Builder(this).setTitle("没有可用的网络")
					.setMessage("是否对网络进行设置？").setCancelable(false);
			b.setPositiveButton("是", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent intent = null;
					// 判断手机系统的版本即API大于10就是3.0或以上ba
					if (android.os.Build.VERSION.SDK_INT > 10) {
						intent = new Intent(
								android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					} else {
						intent = new Intent();
						ComponentName component = new ComponentName(
								"com.android.settings",
								"com.android.settings.WirelessSettings");
						intent.setComponent(component);
						intent.setAction("android.intent.action.VIEW");
					}
					context.startActivity(intent);
					// ComponentName comp = new ComponentName(
					// "com.android.settings",
					// "com.android.settings.WirelessSettings");
					// mIntent.setComponent(comp);
					// mIntent.setAction("android.intent.action.VIEW");
					// startActivityForResult(mIntent, 0); //
					// 如果在设置完成后需要再次进行操作，可以重写操作代码，在这里不再重写
				}
			}).setNeutralButton("否", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
					new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										Intent intent = new Intent(StartLoginActivity.this,
												LoginActivity.class);
										startActivity(intent);
										StartLoginActivity.this.finish();
									}
								}, 2000);// 停止2秒
				}
			}).show();
		}*/

		return netSataus;
	}
}
