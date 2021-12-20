package io.appactive.java.api.channel;

import io.appactive.java.api.base.extension.SPI;

@SPI
public interface ConfigWriteDataSource<T> {

    void write(T value) throws Exception;

    void close() throws Exception;
}
