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
