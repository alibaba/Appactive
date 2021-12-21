package io.appactive.demo.product.service;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.ProductServiceNormal;
import io.appactive.demo.product.repository.ProductRepository;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@DubboService(version = "1.0.0", group = "appactive")
public class ProductServiceNormalImpl implements ProductServiceNormal {

    @Value("${io.appactive.demo.unit}")
    private String unit;

    @Autowired
    ProductRepository productRepository;

    @Override
    public ResultHolder<List<Product>> list() {
        return new ResultHolder<>(productRepository.findAll());
    }

}
