package io.appactive.rpc.springcloud.common.consumer;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mageekchiu
 * @see FeignClientsConfiguration
 */
@Configuration
public class ConsumerAutoConfig {

    private static final Logger logger = LogUtil.getLogger();


    @Autowired
    ApplicationContext context;

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired(required = false)
    RestTemplate restTemplate;


    @Bean
    @ConditionalOnMissingBean
    public Decoder appActiveFeignDecoder() {
        return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters)));
    }
    @Bean
    public BeanPostProcessor feignDecoderPostProcessor() {
        return new FeignDecoderPostProcessor(context);
    }
    // @Bean
    // public BeanPostProcessor serverListPostProcessor(ConfigurableApplicationContext context) {
    //     return new ServerListPostProcessor(context);
    // }
    @Bean
    public RequestInterceptor routerIdTransmissionRequestInterceptor() {
        return new RouterIdTransmissionRequestInterceptor();
    }


    @PostConstruct
    public void init(){
        if (restTemplate != null){
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            if (CollectionUtils.isEmpty(interceptors)) {
                interceptors = new ArrayList<>();
            }
            interceptors.add(new ReqResInterceptor());
            logger.info("ConsumerAutoConfig adding interceptor for restTemplate[{}]......",restTemplate.getClass());
            restTemplate.setInterceptors(interceptors);
        }
    }

}
