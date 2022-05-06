package io.appactive.demo.frontend.filter;

import com.netflix.loadbalancer.Server;
import io.appactive.rpc.springcloud.common.UriContext;
import io.appactive.rpc.springcloud.common.consumer.ConsumerRouter;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author mageekchiu
 */
// @Aspect
// @Component
// public class ServerListFilterAspect {
//     private static final Logger logger = LogUtil.getLogger();
//
//     @Around("execution(* com.netflix.loadbalancer.BaseLoadBalancer.getAllServers(..))"
//             + "|| execution(* com.netflix.loadbalancer.BaseLoadBalancer.getReachableServers(..))"
//             // + "|| execution(* io.appactive.demo.frontend.controller.FrontController.detailProduct(..))"
//     )
//     public List<Server> around(ProceedingJoinPoint pjp){
//         logger.info("ServerListFilterAspect {}", pjp.getSignature());
//         List<Server> finalServers = null;
//         try {
//             Object result = pjp.proceed();
//             if (result instanceof List){
//                 List list = (List)result;
//                 if (CollectionUtils.isNotEmpty(list) && list.get(0) instanceof Server){
//                     List<Server> servers = (List<Server>)list;
//                     logger.info("origin servers {}", servers);
//                     finalServers = ConsumerRouter.filter(servers);
//                     // clear uri after servers are filtered
//                     UriContext.clearContext();
//                     logger.info("filtered servers {}", servers);
//                 }
//             }
//         } catch (Throwable th) {
//             logger.error("error getting origin server list ", th);
//         }
//         return finalServers;
//     }
// }
