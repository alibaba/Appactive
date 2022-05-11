package io.appactive.rpc.springcloud.common.consumer;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import io.appactive.rpc.springcloud.common.UriContext;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Support Response Interceptors · Issue #1126 · OpenFeign/feign: https://github.com/OpenFeign/feign/issues/1126
 *
 * a workaround for the lack of ResponseInterceptor
 */
public class ResponseInterceptor implements Decoder {

    private static final Logger logger = LogUtil.getLogger();

    final Decoder delegate;

    public ResponseInterceptor(Decoder delegate) {
        Objects.requireNonNull(delegate, "Decoder must not be null. ");
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        Object r = delegate.decode(response,type);
        logger.info("ResponseInterceptor uri {} for request {} got cleared by {}", UriContext.getUriPath(), response.request().url(),delegate.getClass());
        UriContext.clearContext();
        return r;
    }
}
