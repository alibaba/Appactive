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

package io.appactive.support.lang.number;

public interface NumComparator<T extends Number> {

    /**
     * 是否符合预期
     * 
     * @param value1
     * @param value2
     * @return
     */
    boolean isMatched(T value1, T value2);

    /**
     * 期望:[{0} {1} {2}]，实际:[{3} {4} {5}]
     * 
     * @param name1
     * @param value1
     * @param name2
     * @param value2
     * @return
     */
    Object[] params(String name1, T value1, String name2, T value2);
}
