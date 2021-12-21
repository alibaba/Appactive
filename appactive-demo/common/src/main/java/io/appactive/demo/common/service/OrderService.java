package io.appactive.demo.common.service;

import io.appactive.demo.common.entity.ResultHolder;

public interface OrderService {

    ResultHolder<String> buy(String rId, String pId, Integer number);

}
