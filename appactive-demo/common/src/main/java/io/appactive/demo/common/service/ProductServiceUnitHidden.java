package io.appactive.demo.common.service;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;

public interface ProductServiceUnitHidden {

    ResultHolder<Product> detail(String pId);
}
