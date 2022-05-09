package io.appactive.rpc.springcloud.common.consumer;

import feign.Feign;
import feign.codec.Decoder;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 */
@Component
public class FeignDecoderPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LogUtil.getLogger();

    @Autowired
    ApplicationContext context;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        /// logger.info("postProcessAfterInitialization {}, {}",beanName, bean.getClass());
        // todo : there does`t have to be a Decoder, so this might fail
        if (bean instanceof Decoder){
            logger.info("replacing bean Decoder......");
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

        // Object o = context.getBean("default.io.appactive.demo.frontend.FrontendApplication.FeignClientSpecification");
        // logger.info("{}",o);

        return bean;
    }
}
