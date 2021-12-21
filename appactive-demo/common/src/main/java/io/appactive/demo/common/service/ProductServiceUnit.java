package io.appactive.demo.common.service;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;

import java.util.List;

public interface ProductServiceUnit {

    ResultHolder<Product> detail(String rId, String pId);
}
