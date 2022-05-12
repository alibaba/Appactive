package io.appactive.rpc.springcloud.common.consumer;

import io.appactive.java.api.base.AppContextClient;
import io.appactive.rpc.springcloud.common.Constants;
import io.appactive.rpc.springcloud.common.UriContext;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @author mageekchiu
 */
public class ReqResInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LogUtil.getLogger();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().add(Constants.ROUTER_ID_HEADER_KEY, AppContextClient.getRouteId());
        UriContext.setUriPath(request.getURI().getPath());

        ClientHttpResponse response = execution.execute(request, body);

        logger.info("ReqResInterceptor uri {} for request {} got cleared", UriContext.getUriPath(), request.getURI());
        UriContext.clearContext();
        return response;
    }
}
