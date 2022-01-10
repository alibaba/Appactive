/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
