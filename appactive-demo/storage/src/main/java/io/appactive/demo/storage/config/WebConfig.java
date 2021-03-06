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

package io.appactive.demo.storage.config;

import io.appactive.rpc.springcloud.common.consumer.ConsumerAutoConfig;
import io.appactive.rpc.springcloud.common.provider.CenterServiceFilter;
import io.appactive.rpc.springcloud.common.provider.UnitServiceFilter;
import io.appactive.rpc.springcloud.nacos.NacosAutoConfig;
import io.appactive.servlet.RequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({NacosAutoConfig.class})
public class WebConfig {
    @Bean
    public FilterRegistrationBean<RequestFilter> appActiveWebFilter() {
        FilterRegistrationBean<RequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        RequestFilter reqResFilter = new RequestFilter();
        filterRegistrationBean.setFilter(reqResFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<CenterServiceFilter> appActiveCenterServiceFilter() {
        FilterRegistrationBean<CenterServiceFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        CenterServiceFilter reqResFilter = new CenterServiceFilter();
        filterRegistrationBean.setFilter(reqResFilter);
        filterRegistrationBean.addUrlPatterns("/buy/*");
        return filterRegistrationBean;
    }
}
