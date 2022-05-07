package io.appactive.rpc.springcloud.common.consumer;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.rpc.springcloud.common.Constants;
import io.appactive.rpc.springcloud.common.UriContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class RouterIdTransmissionRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        if (request == null) {
            return;
        }
        requestTemplate.header(Constants.ROUTER_ID_HEADER_KEY, AppContextClient.getRouteId());
        // store uri for routing filter
        UriContext.setUriPath(requestTemplate.url());
    }
}
