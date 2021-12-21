package io.appactive.demo.product;

import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.ProductServiceCenter;
import io.appactive.demo.common.service.ProductServiceNormal;
import io.appactive.demo.common.service.ProductServiceUnit;
import io.appactive.demo.common.service.ProductServiceUnitHidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@EntityScan("io.appactive.demo.*")
@Controller("/")
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

    @Autowired
    private ProductServiceNormal productServiceNormal;

    @Autowired
    private ProductServiceUnit productServiceUnit;

    @Autowired
    private ProductServiceUnitHidden productServiceUnitHidden;

    @Autowired
    private ProductServiceCenter productServiceCenter;

    @Value("${spring.application.name}")
    private String appName;

    @RequestMapping("/echo")
    @ResponseBody
    public String echo(@RequestParam(required = false, defaultValue = "jack") String user) {
        String s = String.valueOf(user);
        return String.format("%s get %s",s , productServiceNormal.list().toString());
    }

    @RequestMapping(value = "/detail")
    @ResponseBody
    public ResultHolder<Product> detail(@RequestParam(required = false, defaultValue = "12") String pId) {
        // unit
        return productServiceUnitHidden.detail(pId);
    }

    @RequestMapping("/buy")
    @ResponseBody
    public String buy(
            @RequestParam(required = false, defaultValue = "jack") String rId,
            @RequestParam(required = false, defaultValue = "12") String pId,
            @RequestParam(required = false, defaultValue = "5") Integer number
    ) {
        return String.format("%s bought %d %s, result: %s", rId, number, pId ,productServiceCenter.buy(rId, pId, number));
    }

    @RequestMapping("/check")
    @ResponseBody
    public String check() {
        return "OK From "+appName;
    }
}
