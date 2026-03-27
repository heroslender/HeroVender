package com.github.heroslender.herovender.utils;

import com.google.common.base.Strings;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberUtil {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.##");
    public static String[] NumberFormatShortSuffix = "K;M;B;T;Q".split(";");

    public static String format(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    public static String formatShort(double value) {
        return value < 1000 ? format(value) : formatShort(value, 0);
    }

    private static String formatShort(double n, int iteration) {
        double f = n / 1000.0D;
        return f < 1000 || iteration >= NumberFormatShortSuffix.length - 1 ? format(f) + NumberFormatShortSuffix[iteration] : formatShort(f, iteration + 1);
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
            doub = NumberFormat.getInstance().parse(string).doubleValue();
        } catch (ParseException | NumberFormatException e) {
            return -1;
        }

        if (doub < 0 || Double.isInfinite(doub) || Double.isNaN(doub)) {
            return -1;
        }

        return doub;
    }
}
