package com.lu.mbcontrol.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LU on 2018/4/26.
 */

public class PhoneNumberChecked {

    public static boolean isMobile(String mobiles) {
        Pattern p = Pattern.compile("^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
