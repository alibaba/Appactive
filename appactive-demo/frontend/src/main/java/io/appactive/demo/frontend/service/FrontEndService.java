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
