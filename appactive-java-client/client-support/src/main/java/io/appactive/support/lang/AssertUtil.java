package io.appactive.support.lang;

import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.lang.number.NumComparator;
import io.appactive.support.lang.number.impl.NumComparators;

public class AssertUtil {

    public static <T extends Number> void assertGreatZero(String name, T value) {
        assertNotBlank(name, value);
        if (NumberUtil.isInteger(value)) {
            assertNumber(name, value.longValue(), "0", 0L, new NumComparators.GreatEqualsComparator<Long>());
        } else {
            assertNumber(name, value.doubleValue(), "0", 0D, new NumComparators.GreatEqualsComparator<Double>());
        }
    }


    public static <T extends Number> void assertGreatEquals(String name1, String name2, T value1, T value2) {
        assertNotBlank(name1, value1);
        assertNotBlank(name2, value2);
        assertNumber(name1, value1, name2, value2, new NumComparators.GreatEqualsComparator<>());
    }



    public static <T extends Number> void assertNumber(String name1, T value1, String name2, T value2, NumComparator<T> numberCompare) {
        assertNotBlank(name1, value1 );
        assertNotBlank(name2, value2);

        if (!numberCompare.isMatched(value1, value2)) {
            throw ExceptionFactory.makeFault(String.format("not matched: %s-%s",value1,value2));

        }
    }

    public static void assertNotBlank(String name, Object value) {
        if (value != null){
            if (!(value instanceof String)) {
                return;
            }
            // STR
            String str = (String)value;
            if (StringUtils.isNotBlank(str)) {
                return;
            }
        }
        throw ExceptionFactory.makeFault(name+".value is null");
    }
}
