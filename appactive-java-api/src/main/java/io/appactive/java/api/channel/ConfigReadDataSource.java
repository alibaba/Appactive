package io.appactive.java.api.channel;

import io.appactive.java.api.base.extension.SPI;
import io.appactive.java.api.channel.listener.DataListener;

@SPI
public interface ConfigReadDataSource<T> {

    /**
     * get rule config
     * @return config，
     */
    T read() throws Exception;

    void addDataChangedListener(DataListener<T> listener);

    void close() throws Exception;
}
