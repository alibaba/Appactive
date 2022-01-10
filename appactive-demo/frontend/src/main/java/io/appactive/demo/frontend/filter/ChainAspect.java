/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
