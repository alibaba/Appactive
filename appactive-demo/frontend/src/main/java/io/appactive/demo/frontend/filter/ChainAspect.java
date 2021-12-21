package io.appactive.demo.frontend.filter;

import io.appactive.demo.common.entity.ResultHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ChainAspect {

    @AfterReturning(pointcut="execution(* io.appactive.demo.frontend.service.*.*(..))" ,returning = "result")
    public void afterRunning(JoinPoint joinPoint, Object result){
        Object[] args = joinPoint.getArgs();
        String name = joinPoint.getSignature().getName();
        if (result instanceof ResultHolder){
            ResultHolder resultHolder = (ResultHolder)result;
            resultHolder.addChain(System.getenv("io.appactive.demo.app"),System.getenv("io.appactive.demo.unit"));
        }
    }
}
