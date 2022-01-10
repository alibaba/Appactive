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

package io.appactive.java.api.bridge.rpc.constants.bo;

import java.util.Map;
import java.util.Objects;

public class RPCInvokerBO<T> {

    /**
     * 原始数据，用于过滤后归类使用
     */
    private T originalValue;

    /**
     * 实例 元数据信息，用于 标签过滤使用
     * 例如： ip、site、unit、region、 等等
     */
    private Map<String,String> instanceMetaMap;


    /**
     * 服务 元数据信息，用于 标签过滤使用
     * 例如 writeMde、route 等等
     */
    private Map<String,String> serviceMetaMap;

    /**
     * 不确定 或 无法区分归属的默认 meta
     * 在这里范围需要保证其 key 不冲突
     */
    private Map<String,String> defaultMetaMap;

    public T getOriginalValue() {
        return originalValue;
    }

    public RPCInvokerBO setOriginalValue(T originalValue) {
        if (originalValue != null) { this.originalValue = originalValue;}
        return this;
    }

    public Map<String, String> getInstanceMetaMap() {
        return instanceMetaMap;
    }

    public RPCInvokerBO setInstanceMetaMap(Map<String, String> instanceMetaMap) {
        if (instanceMetaMap != null) { this.instanceMetaMap = instanceMetaMap;}
        return this;
    }

    public Map<String, String> getServiceMetaMap() {
        return serviceMetaMap;
    }

    public RPCInvokerBO setServiceMetaMap(Map<String, String> serviceMetaMap) {
        if (serviceMetaMap != null) { this.serviceMetaMap = serviceMetaMap;}
        return this;
    }

    public Map<String, String> getDefaultMetaMap() {
        return defaultMetaMap;
    }

    public RPCInvokerBO setDefaultMetaMap(Map<String, String> defaultMetaMap) {
        if (defaultMetaMap != null) { this.defaultMetaMap = defaultMetaMap;}
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RPCInvokerBO<?> that = (RPCInvokerBO<?>)o;
        return Objects.equals(originalValue, that.originalValue) && Objects.equals(instanceMetaMap,
            that.instanceMetaMap) && Objects.equals(serviceMetaMap, that.serviceMetaMap) && Objects
            .equals(defaultMetaMap, that.defaultMetaMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalValue, instanceMetaMap, serviceMetaMap, defaultMetaMap);
    }

    @Override
    public String toString() {
        return "RPCInvokerBO{" +
            "originalValue=" + originalValue +
            ", instanceMetaMap=" + instanceMetaMap +
            ", serviceMetaMap=" + serviceMetaMap +
            ", defaultMetaMap=" + defaultMetaMap +
            '}';
    }
}
