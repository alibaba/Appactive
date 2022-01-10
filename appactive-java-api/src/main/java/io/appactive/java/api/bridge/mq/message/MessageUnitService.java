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

package io.appactive.java.api.bridge.mq.message;

public interface MessageUnitService<T> {

    /**
     * topic have unit attribute?
     * @param message message obj
     * @return have? true:false
     */
    boolean isUnitTopic(T message);


    /**
     * group have unit attribute?
     * @param message message obj
     * @return have? true:false
     */
    boolean isUnitGroup(T message);


    /**
     * message route type
     * @param message message obj
     * @return route type
     */
    String getRouteType(T message);


    /**
     * message route id
     * @param message message obj
     * @return route id
     */
    String getRouteId(T message);


    /**
     * message produce unit
     * @param message message obj
     * @return produce unit
     */
    String getMsgUnit(T message);
}
