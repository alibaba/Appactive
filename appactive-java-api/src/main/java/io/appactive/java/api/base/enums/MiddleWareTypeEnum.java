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

package io.appactive.java.api.base.enums;

/**
 */
public enum MiddleWareTypeEnum {
    /**
     * open rocketmq
     */
    OPEN_ROCKETMQ("open_rocketmq"),
    SPRING_CLOUD("spring_cloud"),
    DUBBO("dubbo"),
    ;

    private final String value;

    public String getValue() {
        return value;
    }

    MiddleWareTypeEnum(String value) {
        this.value = value;
    }

    public static MiddleWareTypeEnum of(String value){
        if (value == null){
            return null;
        }
        for (MiddleWareTypeEnum typeEnum : MiddleWareTypeEnum.values()) {
            if (typeEnum.getValue().equalsIgnoreCase(value)){
                return typeEnum;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "MiddleWareTypeEnum{" +
            "value='" + value + '\'' +
            '}';
    }
}
