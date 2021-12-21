package io.appactive.demo.common.service;

import io.appactive.demo.common.entity.ResultHolder;


public interface ProductServiceCenter {

    ResultHolder<String> buy(String rId, String pId, int number);

}
