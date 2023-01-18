package com.heroslender.herovender.nms.v1_13_R1.utils;

import com.google.common.base.Strings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class NumberUtil {
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DECIMAL_FORMAT = new DecimalFormat("###,###.##", otherSymbols);
    }

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double getValidMoney(String string) {
        if (Strings.isNullOrEmpty(string))
            return -1;

        string = string.trim();

        if (string.equalsIgnoreCase("nan") || string.equalsIgnoreCase("infinity") || string.equalsIgnoreCase("-infinity")) {
            return -1;
        }

        double doub;

        try {
            doub = DECIMAL_FORMAT.parse(string).doubleValue();
        } catch (ParseException | NumberFormatException e) {
            return -1;
        }

        if (doub < 0 || Double.isInfinite(doub) || Double.isNaN(doub)) {
            return -1;
        }

        return doub;
    }
}
