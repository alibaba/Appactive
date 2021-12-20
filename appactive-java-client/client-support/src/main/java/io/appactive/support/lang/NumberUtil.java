package io.appactive.support.lang;

import java.math.BigDecimal;

public class NumberUtil {

    public static final <T extends Number> boolean isInteger(T value) {
        boolean isInteger = (value instanceof Long) || (value instanceof Integer) || (value instanceof Short) || (value instanceof Byte);
        return isInteger;
    }


    public static final double doubleValue(Double val) {
        double newVal = val == null ? 0 : val.doubleValue();
        return newVal;
    }

    public static final double round(Double doubleValue, int scale) {
        if (doubleValue == null) {
            return 0D;
        }
        BigDecimal bigValue = bidDecimalVal(doubleValue);
        return round(bigValue,scale);
    }

    private static final BigDecimal bidDecimalVal(Double val) {
        double doubleVal = doubleValue(val);
        return BigDecimal.valueOf(doubleVal).setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    public static double round(BigDecimal bdVal, int scale) {
        if (bdVal == null) {
            return 0D;
        }
        return bdVal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
