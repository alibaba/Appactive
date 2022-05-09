package io.appactive.rpc.springcloud.common.consumer;

import com.netflix.loadbalancer.Server;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
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

    /**
     * filtering servers for every ribbon request
     * @param pjp ProceedingJoinPoint
     * @return filtered servers
     */
    @Around("execution(* com.netflix.loadbalancer.BaseLoadBalancer.getAllServers(..))"
            + "|| execution(* com.netflix.loadbalancer.BaseLoadBalancer.getReachableServers(..))"
    )
    public List<Server> around(ProceedingJoinPoint pjp){
        logger.trace("ServerListFilterInterceptor at around {}", pjp.getSignature());
        List<Server> finalServers = null;
        try {
            Object result = pjp.proceed();
            if (result instanceof List){
                List list = (List)result;
                if (CollectionUtils.isNotEmpty(list) && list.get(0) instanceof Server){
                    List<Server> servers = (List<Server>)list;
                    logger.trace("origin servers {}", servers);
                    finalServers = ConsumerRouter.filter(servers);
                    logger.trace("filtered servers {}", finalServers);
                }
            }
        } catch (Throwable th) {
            logger.error("error filtering server list ", th);
        }
        return finalServers;
    }

    /**
     * refresh local server list cache when servers changes
     * @param jp JoinPoint
     */
    @After("execution(* com.netflix.loadbalancer.BaseLoadBalancer.setServersList(..))"
    )
    public void after(JoinPoint jp){
        if (!"com.netflix.loadbalancer.BaseLoadBalancer".equals(jp.getTarget().getClass().getName())){
            // avoid subclass triggering
            return;
        }
        logger.info("ServerListFilterInterceptor at after {}", jp.getSignature());
        Object[] args = jp.getArgs();
        if (args.length > 0){
            List<Server> servers = (List<Server>)args[0];
            Integer num = ConsumerRouter.refresh(servers);
            logger.info("new servers {}, updated {} services[app+uri]",servers, num);
        }
    }
}
