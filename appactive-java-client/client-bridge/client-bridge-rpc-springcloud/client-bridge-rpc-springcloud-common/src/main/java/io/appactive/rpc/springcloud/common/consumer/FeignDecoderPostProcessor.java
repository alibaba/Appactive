package io.appactive.rpc.springcloud.common.consumer;

import feign.codec.Decoder;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 */
public class FeignDecoderPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LogUtil.getLogger();

    final ApplicationContext context;
    public FeignDecoderPostProcessor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // there does`t have to be a Decoder(when using default), so we added a default
        if (bean instanceof Decoder){
            if ("appActiveFeignDecoder".equals(beanName)){
                logger.info("FeignDecoderPostProcessor replacing defaultDecoder {} ......",beanName);
            }else {
                logger.info("FeignDecoderPostProcessor replacing customizedDecoder {} ......",beanName);
            }
            Decoder decoder = (Decoder) bean;
            // wrap original decoder
            return new ResponseInterceptor(decoder);

            /// another way
            // Object proxy = Proxy.newProxyInstance(bean.getClass().getClassLoader(),
            //         bean.getClass().getInterfaces(),
            //         (proxy1, method, args) -> {
            //             String result = (String) method.invoke(bean, args);
            //             return result.toUpperCase();
            //         });
            // return proxy;
        }
        return bean;
    }
}
