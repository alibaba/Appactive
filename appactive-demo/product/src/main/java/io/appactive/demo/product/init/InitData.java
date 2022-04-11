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

package io.appactive.demo.product.init;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static io.appactive.demo.common.Constants.CENTER_FLAG;

@Component
public class InitData implements ApplicationRunner {

    @Resource
    ProductRepository productRepository;

    @Value("${appactive.unit}")
    private String unit;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!CENTER_FLAG.equals(unit)){
            return;
        }

        List<Product> products = new ArrayList<>(4);
        Product p1 = new Product();
        p1.setId("12");
        p1.setName("12 345345fgsdf");
        p1.setDescription("453453sdfsdfsdfdy567fghfdhgsgsdg是粉色的v奋斗");
        p1.setPrice(1559);
        p1.setNumber(10);
        products.add(p1);

        Product p2 = new Product();
        p2.setId("14");
        p2.setName("14 sdgdfhgdfhfdg");
        p2.setDescription("sdfsdfsdfsdfdsfsdfsd豆腐干和水淀粉混合");
        p2.setPrice(1099);
        p2.setNumber(2);
        products.add(p2);

        Product p3 = new Product();
        p3.setId("16");
        p3.setName("16 fghdsgdgdfgdf");
        p3.setDescription("不是大风刮大风个好地方恢复光滑");
        p3.setPrice(1609);
        p3.setNumber(5);
        products.add(p3);

        Product p4 = new Product();
        p4.setId("18");
        p4.setName("18 fdgsddfgsdg");
        p4.setDescription("ga是获得很获得体育馆和健康干净");
        p4.setPrice(1000);
        p4.setNumber(6);
        products.add(p4);

        try {
            productRepository.deleteAll();
            productRepository.saveAll(products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
