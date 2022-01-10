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

package io.appactive.support.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import io.appactive.java.api.base.exception.ExceptionFactory;


public class SpiUtil {

    public static <T> T loadFirstInstance(Class<T> service){
        ServiceLoader<T> load = ServiceLoader.load(service);
        T currentService = null;
        Iterator<T> iterator = load.iterator();
        while(iterator.hasNext()) {
            T ser = iterator.next();
            currentService = ser;
            if (currentService != null){
                break;
            }
        }
        if (currentService == null){
            throw ExceptionFactory.makeFault("Load service failed,it is null:"+service.getName());
        }
        return currentService;
    }

}
