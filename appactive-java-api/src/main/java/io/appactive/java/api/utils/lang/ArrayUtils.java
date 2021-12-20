package io.appactive.java.api.utils.lang;

import java.lang.reflect.Array;

public class ArrayUtils {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static <T> boolean isEmpty(final T[] array) {
        return getLength(array) == 0;
    }
    public static <T> boolean isNotEmpty(final T[] array) {
        return !isEmpty(array);
    }

    public static <T> int getLength(final T array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }
}
