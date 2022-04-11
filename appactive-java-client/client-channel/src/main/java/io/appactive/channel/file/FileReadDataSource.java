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

package io.appactive.channel.file;

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

import com.alibaba.fastjson.JSON;

import io.appactive.java.api.channel.ConfigReadDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.support.log.LogUtil;
import io.appactive.support.thread.SafeWrappers;
import io.appactive.support.thread.ThreadPoolService;

public class FileReadDataSource<T> implements ConfigReadDataSource<T> {

    private final File file;

    private final Charset charset;

    private byte[] buf;

    private final ConverterInterface<String, T> converterInterface;

    private List<DataListener<T>> dataListeners = new ArrayList<>();

    /**
     * TimeUnit.MILLISECONDS
     * file read period
     */
    private long timerPeriod = 3000L;

    private T memoryValue = null;
    private long lastModified = 0L;

    public FileReadDataSource(String filePath, Charset charset, byte[] buf,
                              ConverterInterface<String, T> converterInterface) {
        this(new File(filePath), charset, buf, converterInterface);
    }

    public FileReadDataSource(String filePath, Charset charset, int bufSize,
                              ConverterInterface<String, T> converterInterface) {
        this(new File(filePath), charset, new byte[bufSize], converterInterface);
    }

    public FileReadDataSource(File file, Charset charset, byte[] buf,
                              ConverterInterface<String, T> converterInterface) {
        this(file,charset,buf,null,converterInterface);
    }
    public FileReadDataSource(File file, Charset charset, byte[] buf,Long timerPeriod,
                              ConverterInterface<String, T> converterInterface) {
        this.file = file;
        this.charset = charset;
        this.buf = buf;
        if (timerPeriod != null && timerPeriod > 0){
            this.timerPeriod = timerPeriod;
        }
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
            T valueFromFile = getValueFromSource();
            listenerNotify(oldValue,valueFromFile);
            memoryValue = valueFromFile;
        } catch (IOException e) {
            LogUtil.error("file-read-failed,e:"+e.getMessage(),e);
        }
    }

    private boolean isModified() {
        long curLastModified = file.lastModified();
        if (curLastModified != this.lastModified) {
            this.lastModified = curLastModified;
            return true;
        }
        return false;
        //return true;
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
    public T getValueFromSource() throws IOException {
        if (file == null) {
            LogUtil.warn("[FileReadDataSource] File is null");
            return null;
        }
        if (!file.exists() || !file.isFile()) {
            LogUtil.warn(
                String.format("[FileReadDataSource] File not exist or is not file: %s", file.getAbsolutePath()));
            return null;
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            FileChannel channel = inputStream.getChannel();
            if (channel.size() > buf.length) {
                throw new IllegalStateException(file.getAbsolutePath() + " file size=" + channel.size()
                    + ", is bigger than bufSize=" + buf.length + ". Can't read");
            }
            int len = inputStream.read(buf);
            if (len < 0){
                return null;
            }
            String s = new String(buf, 0, len, charset);
            T convert = converterInterface.convert(s);
            return convert;
        }catch (Exception e){
            LogUtil.warn("FileReadDataSource Exception: {}",e.getCause());
            return null;
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        buf = null;
    }
}
