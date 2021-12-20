package io.appactive.java.api.channel.listener;

/**
 */
public interface DataListener<T> {

    String getListenerName();

    void dataChanged(T oldData,T newData);
}
