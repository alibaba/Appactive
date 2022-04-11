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

package io.appactive.channel.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import io.appactive.java.api.channel.ConfigReadDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.support.log.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

public class NacosReadDataSource<T> implements ConfigReadDataSource<T> {

    private final ConverterInterface<String, T> converterInterface;

    private List<DataListener<T>> dataListeners = new ArrayList<>();

    private String serverAddr;
    private String dataId;
    private String groupId;
    private String namespaceId;

    private ConfigService configService;

    /**
     * TimeUnit.MILLISECONDS
     */
    private long timerPeriod = 3000L;
    private T memoryValue = null;
    private long lastModified = 0L;
    private long curLastModified = -1L;


    public NacosReadDataSource(String serverAddr, String dataId, String groupId, ConverterInterface<String, T> converterInterface) {
        this(serverAddr,dataId,groupId,"", converterInterface);
    }

    public NacosReadDataSource(String serverAddr, String dataId, String groupId, String namespaceId, ConverterInterface<String, T> converterInterface) {
        this.serverAddr = serverAddr;
        this.dataId = dataId;
        this.groupId = groupId;
        this.namespaceId = namespaceId;
        this.converterInterface = converterInterface;

        startTimerService();
        initMemoryValue();
    }

    private void startTimerService() {
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            properties.put(PropertyKeyConst.NAMESPACE, namespaceId);
            configService = NacosFactory.createConfigService(properties);

            configService.addListener(dataId, groupId, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    LogUtil.warn("get Nacos configInfo {}", configInfo);
                    lastModified = System.currentTimeMillis();
                    initMemoryValue();
                }
                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            LogUtil.error("get Nacos configService Exception ", e);
        }
    }


    private void initMemoryValue() {
        if (!isModified()) {
            return;
        }
        try {
            T oldValue = memoryValue;
            T newValue = getValueFromSource();
            listenerNotify(oldValue,newValue);
            memoryValue = newValue;
        } catch (IOException e) {
            LogUtil.error("nacos-read-failed,e:"+e.getMessage(),e);
        }
    }

    private boolean isModified() {
        if (curLastModified != this.lastModified) {
            this.curLastModified = lastModified;
            return true;
        }
        return false;
    }

    @Override
    public T read() throws Exception {
        return memoryValue;
    }

    @Override
    public void addDataChangedListener(DataListener<T> listener) {
        if (listener == null){
            return;
        }
        dataListeners.add(listener);
        listener.dataChanged(null,memoryValue);
    }

    @Override
    public List<DataListener<T>> getDataListeners() {
        return dataListeners;
    }

    @Override
    public T getValueFromSource() throws IOException{
        try {
            String content = configService.getConfig(dataId, groupId, timerPeriod);
            return converterInterface.convert(content);
        } catch (NacosException e) {
            LogUtil.warn("getValueFromSource Exception ", e);
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        if (configService!=null){
            configService.shutDown();
        }
    }
}
