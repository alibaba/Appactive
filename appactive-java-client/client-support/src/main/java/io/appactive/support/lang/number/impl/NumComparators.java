package io.appactive.support.lang.number.impl;

import io.appactive.support.lang.NumberUtil;
import io.appactive.support.lang.number.NumComparator;

public class NumComparators {

    /**
     * 类NumValidator.java的实现描述：数据比较基类
     */
    public static abstract class BaseComparator<T extends Number> implements NumComparator<T> {

        /** 正向符合要求的逻辑 */
        protected String expectOperator;

        /** 反向不符合要求的逻辑 */
        protected String oppositeOperator;

        public BaseComparator(String expectOperator, String oppositeOperator){
            this.expectOperator = expectOperator;
            this.oppositeOperator = oppositeOperator;
        }

        @Override
        public boolean isMatched(T value1, T value2) {
            if (NumberUtil.isInteger(value1) && NumberUtil.isInteger(value2)) {
                return this.compare(value1.longValue(), value2.longValue());
            }

            double doubleValue1 = NumberUtil.round(value1.doubleValue(), 6);
            double doubleValue2 = NumberUtil.round(value2.doubleValue(), 6);
            return this.compare(doubleValue1, doubleValue2);
        }

        @Override
        public Object[] params(String name1, T value1, String name2, T value2) {
            Object[] params = null;
            if (NumberUtil.isInteger(value1) && NumberUtil.isInteger(value2)) {
                params = new Object[] { name1, this.expectOperator, name2, value1, this.oppositeOperator, value2 };
            } else {
                Double doubleValue1 = NumberUtil.round(value1.doubleValue(), 6);
                Double doubleValue2 = NumberUtil.round(value2.doubleValue(), 6);
                params = new Object[] { name1, this.expectOperator, name2, doubleValue1, this.oppositeOperator, doubleValue2 };
            }
            return params;
        }

        protected abstract boolean compare(long value1, long value2);

        protected abstract boolean compare(double value1, double value2);
    }

    /**
     * 类NumComparators.java的实现描述：等于条件
     * 
     */
    public static class EqualsComparator<T extends Number> extends BaseComparator<T> {
        private final float THRESHOLD = 0.00000001F;

        public EqualsComparator(){
            super("=", "!=");
        }

        @Override
        protected boolean compare(long value1, long value2) {
            return Math.abs(value1-value2) < THRESHOLD;
        }

        @Override
        protected boolean compare(double value1, double value2) {
            return Math.abs(value1-value2) < THRESHOLD;
        }
    }

    /**
     * 类NumValidator.java的实现描述：基于数字大于目标数字
     * 
     */
    public static class GreatComparator<T extends Number> extends BaseComparator<T> {

        public GreatComparator(){
            super(">", "<=");
        }

        @Override
        protected boolean compare(long baseValue, long targetValue) {
            return baseValue > targetValue;
        }

        @Override
        protected boolean compare(double baseValue, double targetValue) {
            return baseValue > targetValue;
        }
    }

    /**
     * 类NumValidator.java的实现描述：基于数字大于等于目标数字
     * 
     */
    public static class GreatEqualsComparator<T extends Number> extends BaseComparator<T> {

        public GreatEqualsComparator(){
            super(">=", "<");
        }

        @Override
        protected boolean compare(long baseValue, long targetValue) {
            return baseValue >= targetValue;
        }

        @Override
        protected boolean compare(double baseValue, double targetValue) {
            return baseValue >= targetValue;
        }

    }
}
