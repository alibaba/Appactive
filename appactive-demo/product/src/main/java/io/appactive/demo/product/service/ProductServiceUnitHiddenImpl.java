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

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.ProductServiceUnitHidden;
import io.appactive.demo.product.repository.ProductRepository;
import io.appactive.java.api.base.AppContextClient;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@DubboService(version = "1.0.0", group = "appactive", parameters = {"rsActive","unit"})
public class ProductServiceUnitHiddenImpl implements ProductServiceUnitHidden {

    @Value("${appactive.unit}")
    private String unit;

    @Autowired
    ProductRepository productRepository;

    @Override
    public ResultHolder<Product> detail(String pId) {
        String rId = AppContextClient.getRouteId();
        System.out.println("detail: " + pId + ",rId " + rId);
        return new ResultHolder<>(productRepository.findById(pId).orElse(new Product()));
    }
}
