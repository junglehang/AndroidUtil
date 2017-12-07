package com.test.androidutil.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 作者 E-mail:Lynn-2963885227@qq.com
 * @version 创建时间：2016年5月18日 上午9:15:37
 *          类说明
 */
public class IsMobileNumUtil {
    //判断是否是手机号
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();

    }
}
