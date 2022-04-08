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

import com.alibaba.fastjson.JSON;
import io.appactive.java.api.channel.ConfigReadDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.support.log.LogUtil;
import io.appactive.support.thread.SafeWrappers;
import io.appactive.support.thread.ThreadPoolService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NacosReadDataSource<T> implements ConfigReadDataSource<T> {

    private final ConverterInterface<String, T> converterInterface;

    private List<DataListener<T>> dataListeners = new ArrayList<>();

    private String dataId;
    private String groupId;

    /**
     * TimeUnit.MILLISECONDS
     * file read period
     */
    private long timerPeriod = 3000L;

    private T memoryValue = null;
    private long lastModified = 0L;


    public NacosReadDataSource(String dataId, ConverterInterface<String, T> converterInterface) {
        this(dataId, NacosPathUtil.getGroupId(), converterInterface);
    }

    public NacosReadDataSource(String dataId, String groupId, ConverterInterface<String, T> converterInterface) {
        this.dataId = dataId;
        this.groupId = groupId;
        this.converterInterface = converterInterface;
        initMemoryValue();
        startTimerService();
    }

    private void startTimerService() {
        ScheduledExecutorService executorService = ThreadPoolService.createSingleThreadScheduledExecutor(
            "appactive.file-read-value-task");
        executorService.scheduleAtFixedRate(SafeWrappers.safeRunnable(checkFileChanged()), timerPeriod,timerPeriod, TimeUnit.MILLISECONDS);
    }

    private Runnable checkFileChanged() {
        return new Runnable() {
            @Override
            public void run() {
                initMemoryValue();
            }
        };
    }

    private void initMemoryValue() {
        if (!isModified()) {
            // not changed
            return;
        }
        try {
            T oldValue = memoryValue;
            T valueFromFile = getValueFromFile();
            listenerNotify(oldValue,valueFromFile);
            memoryValue = valueFromFile;
        } catch (IOException e) {
            LogUtil.error("file-read-failed,e:"+e.getMessage(),e);
        }
    }

    private boolean isModified() {
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

    private void listenerNotify(T oldValue,T newValue) {
        for (DataListener<T> dataListener : dataListeners) {
            try {
                String listenerName = dataListener.getListenerName();
                String msg = MessageFormat.format("listener data changed,old:{0},new:{1},listener:{2}",
                    JSON.toJSONString(oldValue), JSON.toJSONString(newValue), listenerName);
                LogUtil.info(msg);
                dataListener.dataChanged(oldValue,newValue);
            }catch (Exception e){
                LogUtil.error("dataChanged failed,listener:"+dataListener.toString()+",e:"+e.getMessage(),e);
            }
        }
    }

    private T getValueFromFile() throws IOException {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
