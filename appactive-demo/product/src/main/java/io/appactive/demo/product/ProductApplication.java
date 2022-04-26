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

package io.appactive.demo.product;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.dubbo.ProductServiceCenter;
import io.appactive.demo.common.service.dubbo.ProductServiceNormal;
import io.appactive.demo.common.service.dubbo.ProductServiceUnit;
import io.appactive.demo.common.service.dubbo.ProductServiceUnitHidden;
import io.appactive.java.api.base.AppContextClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@SpringBootApplication
@EntityScan("io.appactive.demo.*")
@Controller("/")
@EnableDiscoveryClient
@EnableFeignClients
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

    @Autowired
    private ProductServiceNormal productServiceNormal;

    @Autowired
    private ProductServiceUnit productServiceUnit;

    @Autowired
    private ProductServiceUnitHidden productServiceUnitHidden;

    @Autowired
    private ProductServiceCenter productServiceCenter;

    @Value("${spring.application.name}")
    private String appName;

    @RequestMapping("/echo")
    @ResponseBody
    public String echo(@RequestParam(required = false, defaultValue = "jack") String user) {
        String s = String.valueOf(user);
        return String.format("%s get %s",s , productServiceNormal.list().toString());
    }

    @RequestMapping("/list")
    @ResponseBody
    public ResultHolder<List<Product>> list() {
        return productServiceNormal.list();
    }


    @RequestMapping(value = "/detailHidden")
    @ResponseBody
    public ResultHolder<Product> detailHidden(@RequestParam(required = false, defaultValue = "12") String pId) {
        // unit
        return productServiceUnitHidden.detail(pId);
    }

    @RequestMapping(value = "/detail")
    @ResponseBody
    public ResultHolder<Product> detail(@RequestParam(required = false, defaultValue = "12") String rId,
                                        @RequestParam(required = false, defaultValue = "12") String pId) {
        // unit
        return productServiceUnit.detail(rId, pId);
    }

    @RequestMapping("/buy")
    @ResponseBody
    public ResultHolder<String> buy(
            @RequestParam(required = false, defaultValue = "jack") String rId,
            @RequestParam(required = false, defaultValue = "12") String pId,
            @RequestParam(required = false, defaultValue = "5") Integer number
    ) {
        return new ResultHolder<>(String.format("routerId %s bought %d of item %s, result: %s", AppContextClient.getRouteId(), number, pId ,productServiceCenter.buy(rId, pId, number)));
    }

    @RequestMapping("/check")
    @ResponseBody
    public String check() {
        return "OK From "+appName;
    }
}
