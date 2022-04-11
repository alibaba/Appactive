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

package io.appactive.java.api.channel;

import io.appactive.java.api.base.extension.SPI;
import io.appactive.java.api.channel.listener.DataListener;

import java.io.IOException;
import java.util.List;

@SPI
public interface ConfigReadDataSource<T> {

    T read() throws Exception;

    void addDataChangedListener(DataListener<T> listener);

    List<DataListener<T>> getDataListeners();

    default void listenerNotify(T oldValue,T newValue){
        for (DataListener<T> dataListener : getDataListeners()) {
            /// for debug
            // String listenerName = dataListener.getListenerName();
            dataListener.dataChanged(oldValue,newValue);
        }
    }

    void close() throws Exception;

    T getValueFromSource() throws IOException;
}
