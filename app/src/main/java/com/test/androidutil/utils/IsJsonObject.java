package com.test.androidutil.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author：Lynn on 2016/9/18 16:28
 * <p/>
 * E-mail：lynn_47253@sina.com
 */

public class IsJsonObject {
    /**
     * 判断字符串是否为Json格式
     * @param result   字符串
     * @return      boolean
     */
    public static boolean isJsonObject(String result){
        return result.contains("{")&&result.endsWith("}");
    }

    /**
     * 将字符串转为Json格式
     * @param sb   Json类型字符串
     * @return  jsonObject
     * @throws JSONException
     */
    public static JSONObject getJSON(String sb) throws JSONException {
        return (new JSONObject(sb));
    }
}
