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

    @Value("${io.appactive.demo.unit}")
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
                        result = "扣减库存失败";
                    }
                }
            }else {
                result = "商品不存在";
            }
        }catch (Exception e){
            result = e.getMessage();
        }
        return new ResultHolder<>(result);
    }
}
