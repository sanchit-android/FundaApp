package com.sanchit.funda.utils;

import java.math.BigDecimal;

public class NumberUtils {

    private static final String MONEY_FORMAT = "%,.1f";
    private static final int SCALE_DISPLAY = 0;

    public static BigDecimal parseNumber(String text) {
        text = text.replaceAll(",", "");
        return new BigDecimal(text);
    }

    public static String formatMoney(BigDecimal money) {
        return String.format(MONEY_FORMAT, money);
    }

    public static String toPercentage(BigDecimal val) {
        BigDecimal percentage = val.multiply(new BigDecimal(100.00)).setScale(SCALE_DISPLAY, BigDecimal.ROUND_HALF_DOWN);
        if (val.compareTo(BigDecimal.ZERO) >= 0) {
            return percentage.toPlainString() + "%";
        } else {
            return "(" + percentage.toPlainString() + ")%";
        }
    }

    public static String toPercentage(BigDecimal val, int scale) {
        BigDecimal percentage = val.multiply(new BigDecimal(100.00)).setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        if (val.compareTo(BigDecimal.ZERO) >= 0) {
            return percentage.toPlainString() + "%";
        } else {
            return "(" + percentage.toPlainString() + ")%";
        }
    }

}
