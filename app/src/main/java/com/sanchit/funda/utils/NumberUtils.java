package com.sanchit.funda.utils;

import java.math.BigDecimal;

public class NumberUtils {

    private static final String MONEY_FORMAT = "%,.2f";
    private static final int SCALE_DISPLAY = 0;
    private static final BigDecimal QUANTITY_TOLERANCE = new BigDecimal("0.5");

    public static BigDecimal safeDivide(BigDecimal num, BigDecimal den, int scale) {
        if (num == null || den == null || BigDecimal.ZERO.equals(num) || BigDecimal.ZERO.equals(den)) {
            return BigDecimal.ZERO;
        }

        return num.divide(den, scale, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal parseNumber(String text) {
        text = text.trim().replaceAll(",", "");
        return new BigDecimal(text);
    }

    public static String formatMoney(BigDecimal money) {
        return BigDecimal.ZERO.equals(money) ? "-" : String.format(MONEY_FORMAT, money);
    }

    public static String formatMoney(BigDecimal money, int rounding) {
        return BigDecimal.ZERO.equals(money) ? "-" : String.format("%,." + rounding + "f", money);
    }

    public static String toPercentage(BigDecimal num, BigDecimal den) {
        if (num == null || den == null || BigDecimal.ZERO.equals(num) || BigDecimal.ZERO.equals(den)) {
            return "0%";
        }

        BigDecimal val = num.divide(den, 2, BigDecimal.ROUND_HALF_UP);
        return toPercentage(val);
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

    public static boolean equals(BigDecimal runningClose, BigDecimal openUntilNow) {
        return openUntilNow.equals(runningClose) || openUntilNow.subtract(runningClose).abs().compareTo(QUANTITY_TOLERANCE) <= 0;
    }

}
