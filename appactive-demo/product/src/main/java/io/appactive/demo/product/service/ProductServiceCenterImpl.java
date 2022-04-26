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

package io.appactive.demo.product.service;

import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.dubbo.OrderService;
import io.appactive.demo.common.service.dubbo.ProductServiceCenter;
import io.appactive.demo.product.repository.ProductRepository;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@DubboService(version = "1.0.0", group = "appactive", parameters = {"rsActive","center","routeIndex","0"})
public class ProductServiceCenterImpl implements ProductServiceCenter {

    @Value("${appactive.unit}")
    private String unit;

    @DubboReference(version = "1.0.0", group = "appactive", check = false)
    private OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @Override
    public ResultHolder<String> buy(String rId, String pId, int number) {
        // center
        System.out.println("product buy: " + rId + " : " + pId);
        return orderService.buy(rId, pId, number);
    }
}
