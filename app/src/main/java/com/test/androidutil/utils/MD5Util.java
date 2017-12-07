package com.test.androidutil.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * author：Lynn on 2016/10/27 10:52
 * <p>
 * E-mail：lynn_47253@sina.com
 */


public class MD5Util {
    private static String TAG = "MD5Util";
    /**
     * MD5加码    生成32位md5码
     */
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.i(TAG, "string2MD5: "+e.toString());
            e.printStackTrace();
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int j = 0; j < md5Bytes.length; j++) {
                int val = ((int) md5Bytes[j]) & 0xff;
                if(val< 16){
                    hexValue.append("0");
                    hexValue.append(Integer.toHexString(val));
                }
            }
        return hexValue.toString();
    }
    /**
     * 加密解密算法   执行一次加密，两次解密
     */
    public static String convertMD5(String inStr){

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i]^'k'^'l'^'L');
        }
        String s = new String(a);
        return s;
    }

}
