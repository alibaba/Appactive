package io.appactive.demo.storage;

import io.appactive.demo.common.service.OrderService;
import io.appactive.java.api.base.AppContextClient;
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
public class StorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }

    @Autowired
    OrderService orderService;

    @Value("${spring.application.name}")
    private String appName;

    @RequestMapping("/buy")
    @ResponseBody
    public String buy(
            @RequestParam(required = false, defaultValue = "jack") String rId,
            @RequestParam(required = false, defaultValue = "12") String id,
            @RequestParam(required = false, defaultValue = "5") Integer number
    ) {
        String routerId = AppContextClient.getRouteId();
        System.out.println("buy:"+routerId);
        return String.format("%s bought %d %s, result: %s %s", rId, number, id ,orderService.buy(rId, id, number), routerId);
    }

    @RequestMapping("/check")
    @ResponseBody
    public String check() {
        return "OK From "+appName;
    }
}
