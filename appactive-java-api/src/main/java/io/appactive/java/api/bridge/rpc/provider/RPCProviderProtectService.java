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

package io.appactive.java.api.bridge.rpc.provider;

import org.apache.dubbo.rpc.Result;

/**
 *
 */
public interface RPCProviderProtectService<T,P,meta> {

    /**
     * check normal service
     * @param invocation the invocation
     * @param serviceMetadata the serviceMetadata
     * @return null: do nothing result: error
     */
    Result normalResourceProtect(T invocation, meta serviceMetadata);

    /**
     * check unit service
     * @param invocation the invocation
     * @param providerModel the providerModel
     * @return null: do nothing result: error
     */
    Result unitResourceProtect(T invocation, P providerModel);

    /**
     * check center service
     * @param invocation the invocation
     * @param serviceMetadata the serviceMetadata
     * @return null: do nothing result: error
     */
    Result centerResourceProtect(T invocation,meta serviceMetadata);
}
