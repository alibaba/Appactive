/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appactive.demo.frontend.controller;

import com.alibaba.fastjson.JSON;
import io.appactive.demo.common.entity.Product;
import io.appactive.demo.common.entity.ResultHolder;
import io.appactive.demo.common.service.ProductServiceUnitHidden;
import io.appactive.demo.frontend.service.FrontEndService;
import io.appactive.java.api.base.AppContextClient;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/")
public class FrontController {

    @Autowired
    private FrontEndService frontEndService;

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private Environment env;

    private Map<String, String[]> metaData ;


    @DubboReference(version = "1.0.0", group = "appactive", check = false)
    private ProductServiceUnitHidden productServiceUnitHidden;

    @GetMapping("/list")
    @ResponseBody
    public ResultHolder<List<Product>> list() {
        // normal
        try {
            ResultHolder<List<Product>> resultHolder = frontEndService.list();
            List<Product> products = resultHolder.getResult();
            return resultHolder;
        } catch (Throwable t) {
            t.printStackTrace();
            return new ResultHolder<>(null);
        }
    }

    @GetMapping(value = "/detail")
    @ResponseBody
    public ResultHolder<Product> detail(@RequestParam(required = false, defaultValue = "12") String id) {
        // unit
        ResultHolder<Product> resultHolder = frontEndService.detail(AppContextClient.getRouteId(), id);
        Product product = resultHolder.getResult();
        return resultHolder;
    }

    @RequestMapping("/buy")
    @ResponseBody
    public ResultHolder<String> buy(
            @RequestParam(required = false, defaultValue = "jack") String user,
            @RequestParam(required = false, defaultValue = "12") String id,
            @RequestParam(required = false, defaultValue = "5") Integer number
    ) {
        // unit
        ResultHolder<String> resultHolder = frontEndService.buy(id, number);
        // return String.format("%s bought %d %s, result: %s", user, number, id ,resultHolder);
        return resultHolder;
    }

    @RequestMapping("/echo")
    @ResponseBody
    public ResultHolder<String> echo(
            @RequestParam(required = false, defaultValue = "echo content") String content
    ) {
        return new ResultHolder<>(appName + " : " +content);
    }

    @RequestMapping("/check")
    @ResponseBody
    public String check() {
        return "OK From "+appName;
    }


    @ModelAttribute("metaData")
    public Map<String,String[]> getMetaData() {
        return metaData;
    }

    @PostConstruct
    public void parseMetaData() {
        String unitList = env.getProperty("io.appactive.demo.unitlist");
        String appList = env.getProperty("io.appactive.demo.applist");
        metaData = new HashMap<>(2);
        metaData.put("unitList",unitList.split(","));
        metaData.put("appList",appList.split(","));
    }

    @RequestMapping("/meta")
    @ResponseBody
    public ResultHolder<Object> meta() {
        return new ResultHolder<>(metaData);
    }

    @GetMapping("/listProduct")
    public String listProduct(Model model) {
        // normal
        ResultHolder<List<Product>> resultHolder = frontEndService.list();
        System.out.println(resultHolder.getChain());
        model.addAttribute("result", JSON.toJSONString(resultHolder.getResult()));
        model.addAttribute("chain", JSON.toJSONString(resultHolder.getChain()));
        model.addAttribute("current", "listProduct");
        return "index.html";
    }

    @GetMapping(value = "/detailProduct")
    public String detailProduct(@RequestParam(required = false, defaultValue = "12") String id,
                                @RequestParam(required = false, defaultValue = "false") Boolean hidden,
                                Model model) {
        // unit
        ResultHolder<Product> resultHolder = hidden ? frontEndService.detailHidden(id) : frontEndService.detail(AppContextClient.getRouteId(), id);
        model.addAttribute("result", JSON.toJSONString(resultHolder.getResult()));
        model.addAttribute("chain", JSON.toJSONString(resultHolder.getChain()));
        model.addAttribute("current", "detailProduct");
        return "detail.html";
    }

    @RequestMapping("/buyProduct")
    public String buyProduct(
            @RequestParam(required = false, defaultValue = "jack") String rId,
            @RequestParam(required = false, defaultValue = "12") String pId,
            @RequestParam(required = false, defaultValue = "5") Integer number,
            Model model
    ) {
        // unit
        ResultHolder<String> resultHolder = frontEndService.buy(pId, number);
        model.addAttribute("result", JSON.toJSONString(resultHolder.getResult()));
        model.addAttribute("chain", JSON.toJSONString(resultHolder.getChain()));
        model.addAttribute("current", "buyProduct");
        return "buy.html";
    }
}
