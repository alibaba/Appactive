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

package io.appactive.demo.storage.service;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.OrderService;
import io.appactive.demo.storage.repository.ProductRepository;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@DubboService(version = "1.0.0", group = "appactive", parameters = {"rsActive","center","routeIndex","0"})
public class OrderServiceImpl implements OrderService {

    @Value("${appactive.unit}")
    private String unit;

    @Autowired
    ProductRepository repository;

    @Override
    public ResultHolder<String> buy(String rId, String pId, Integer number) {
        System.out.println("buy: " + rId + " : " + pId + " : " + number);
        String result = null;
        try {
            Optional<Product> op = repository.findById(pId);
            if (op.isPresent()){
                // todo 扣库存，应该强校验
                Product p = op.get();
                int oldNum = p.getNumber();
                int left = oldNum - number;
                if (left > 0){
                    p.setNumber(left);
                    p = repository.save(p);
                    if (p.getNumber() + number != oldNum){
                        result = "storage not consist";
                    }else {
                        result = "success";
                    }
                }else {
                    result = "out of product";
                }
            }else {
                result = "no such product";
            }
        }catch (Throwable e){
            result = e.getCause().getCause().getMessage();
        }
        return new ResultHolder<>(result);
    }
}
