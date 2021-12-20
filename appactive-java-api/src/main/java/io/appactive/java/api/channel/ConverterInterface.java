package io.appactive.java.api.channel;

public interface ConverterInterface<S,T> {

    /**
     * convert source to target
     * @param source
     * @return
     */
    T convert(S source);
}
