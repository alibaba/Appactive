package io.appactive.demo.product.service;

import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.OrderService;
import io.appactive.demo.common.service.ProductServiceCenter;
import io.appactive.demo.product.repository.ProductRepository;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@DubboService(version = "1.0.0", group = "appactive", parameters = {"rsActive","center","routeIndex","0"})
public class ProductServiceCenterImpl implements ProductServiceCenter {

    @Value("${io.appactive.demo.unit}")
    private String unit;

    @DubboReference(version = "1.0.0", group = "appactive", check = false)
    private OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @Override
    public ResultHolder<String> buy(String rId, String pId, int number) {
        // center
        System.out.println("buy: " + rId + " : " + pId);
        return orderService.buy(rId, pId, number);
    }
}
