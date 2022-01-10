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
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import io.appactive.java.api.channel.ConfigWriteDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.support.log.LogUtil;

public class FileWriteDataSource<T> implements ConfigWriteDataSource<T> {

    private final File file;

    private final Charset charset;

    private final ConverterInterface<T,String> converterInterface;

    public FileWriteDataSource(String filePath, Charset charset,
                               ConverterInterface<T, String> converterInterface) {
       this(new File(filePath),charset,converterInterface);
    }

    public FileWriteDataSource(File file, Charset charset,
                               ConverterInterface<T, String> converterInterface) {
        this.file = file;
        this.charset = charset;
        this.converterInterface = converterInterface;
    }

    @Override
    public void write(T value) throws Exception {
        syncWriteFile(value);
    }

    private void syncWriteFile(T value) throws Exception {
        String convertResult = converterInterface.convert(value);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            byte[] bytesArray = convertResult.getBytes(charset);

            LogUtil.info("[FileWritableDataSource] Writing to file {}: {}", file, convertResult);
            outputStream.write(bytesArray);
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ignore) {
                    // nothing
                }
            }
        }
    }

    @Override
    public void close() throws Exception {

    }
}
