package com.test.androidutil.utils.appUploadUtil;

import com.yifan.shufa.global.GlobalContants;

/**
 * 获取服务器IP地址
 */

public class GetServerUrl{
	static String url=GlobalContants.SERVER_URL+"/app/vertions/android";
	public static String getUrl() {
		return url;
	}
}
