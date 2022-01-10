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

package io.appactive.demo.frontend.service;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.ProductServiceCenter;
import io.appactive.demo.common.service.ProductServiceNormal;
import io.appactive.demo.common.service.ProductServiceUnit;
import io.appactive.demo.common.service.ProductServiceUnitHidden;
import io.appactive.java.api.base.AppContextClient;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontEndService {

    @DubboReference(version = "1.0.0", group = "appactive", check = false)
    private ProductServiceNormal productServiceNormal;

    @DubboReference(version = "1.0.0", group = "appactive", check = false)
    private ProductServiceUnit productServiceUnit;

    @DubboReference(version = "1.0.0", group = "appactive", check = false)
    private ProductServiceUnitHidden productServiceUnitHidden;

    @DubboReference(version = "1.0.0", group = "appactive", check = false)
    private ProductServiceCenter productServiceCenter;

    public ResultHolder<List<Product>> list(){
        return productServiceNormal.list();
    }

    public ResultHolder<Product> detail(String rId, String pId){
        return productServiceUnit.detail(rId, pId);
    }

    public ResultHolder<Product> detailHidden(String pId){
        return productServiceUnitHidden.detail(pId);
    }

    public ResultHolder<String> buy(String pId, int number){
        return productServiceCenter.buy(AppContextClient.getRouteId(), pId, number);
    }
}
