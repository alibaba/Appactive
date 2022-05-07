package io.appactive.rpc.springcloud.common.consumer;

import com.netflix.loadbalancer.Server;
import io.appactive.rpc.springcloud.common.UriContext;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;

import java.util.List;


/**
 * @author mageekchiu
 */
@Aspect
public class ServerListFilterInterceptor {
    private static final Logger logger = LogUtil.getLogger();

    @Around("execution(* com.netflix.loadbalancer.BaseLoadBalancer.getAllServers(..))"
            + "|| execution(* com.netflix.loadbalancer.BaseLoadBalancer.getReachableServers(..))"
    )
    public List<Server> around(ProceedingJoinPoint pjp){
        logger.info("ServerListFilterInterceptor at {}", pjp.getSignature());
        List<Server> finalServers = null;
        try {
            Object result = pjp.proceed();
            if (result instanceof List){
                List list = (List)result;
                if (CollectionUtils.isNotEmpty(list) && list.get(0) instanceof Server){
                    List<Server> servers = (List<Server>)list;
                    logger.info("origin servers {}", servers);
                    finalServers = ConsumerRouter.filter(servers);
                    logger.info("filtered servers {}", finalServers);
                }
            }
        } catch (Throwable th) {
            logger.error("error filtering server list ", th);
        }
        return finalServers;
    }
}
