package io.appactive.demo.common.service.springcloud;

import io.appactive.demo.common.RPCType;
import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Service
public class ProductDAO {

    @Autowired
    private ProductService productService;

    public ResultHolder<List<Product>> list(){
        return productService.list();
    }

    public ResultHolder<Product> detail(String rId, String pId){
        return productService.detail(rId, pId);
    }

    public ResultHolder<Product> detailHidden(String pId){
        return productService.detailHidden(pId);
    }

    public ResultHolder<String> buy(String rId, String pId, Integer number){
        return productService.buy(RPCType.SpringCloud.name(), rId, pId, number);
    }

    @FeignClient(name = "product")
    public interface ProductService {

        @RequestMapping(value = "/list/")
        ResultHolder<List<Product>>  list();

        @RequestMapping("/detail/")
        ResultHolder<Product> detail(@RequestParam(name = "rId") String rId,
                                     @RequestParam(name = "pId") String pId
        );

        @RequestMapping("/detailHidden/")
        ResultHolder<Product> detailHidden(@RequestParam(name = "pId") String pId
        );

        @RequestMapping("/buy/")
        ResultHolder<String> buy(@RequestParam(name = "rpcType") String rpcType,
                                 @RequestParam(name = "rId") String rId,
                                  @RequestParam(name = "pId") String pId,
                                  @RequestParam(name = "number") Integer number
        );

    }
}
