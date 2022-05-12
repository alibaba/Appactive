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

package io.appactive.demo.storage;

import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.dubbo.OrderService;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@EntityScan("io.appactive.demo.*")
@ComponentScan(basePackages = {
        "io.appactive.demo",
})
@Controller("/")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"io.appactive.demo"})
public class StorageApplication {

    private static final Logger logger = LogUtil.getLogger();

    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }

    @Autowired
    OrderService orderService;

    @Value("${spring.application.name}")
    private String appName;

    @RequestMapping("/buy")
    @ResponseBody
    public ResultHolder<String> buy(
            @RequestParam(required = false, defaultValue = "jack") String rId,
            @RequestParam(required = false, defaultValue = "12") String id,
            @RequestParam(required = false, defaultValue = "1") Integer number
    ) {
        String routerId = AppContextClient.getRouteId();
        logger.info("buy, routerId: {}", routerId);
        ResultHolder<String> resultHolder = orderService.buy(rId, id, number);
        resultHolder.setResult(String.format("routerId %s bought %d of item %s, result: %s", routerId, number, id ,resultHolder.getResult()));
        return resultHolder;
    }

    @RequestMapping("/check")
    @ResponseBody
    public String check() {
        return "OK From "+appName;
    }
}
