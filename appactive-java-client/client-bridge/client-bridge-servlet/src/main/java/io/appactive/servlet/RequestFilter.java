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

package io.appactive.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.appactive.java.api.base.constants.AppactiveConstant;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.bridge.servlet.ServletService;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.rule.traffic.IdSourceRuleService;
import io.appactive.java.api.rule.traffic.bo.IdSourceEnum;
import io.appactive.java.api.rule.traffic.bo.IdSourceRule;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rule.ClientRuleService;
import io.appactive.support.lang.CollectionUtils;
import io.appactive.support.log.LogUtil;

/**
 */
public class RequestFilter implements Filter, ServletService {

    private static final Map<IdSourceEnum, BiFunction<HttpServletRequest,String, String>> STRATEGY_MAP = new HashMap<>();
    static {
        STRATEGY_MAP.put(IdSourceEnum.arg, ServletService::getRouteIdFromParams);
        STRATEGY_MAP.put(IdSourceEnum.header, ServletService::getRouteIdFromHeader);
        STRATEGY_MAP.put(IdSourceEnum.cookie, ServletService::getRouteIdFromCookie);
    }

    private static IdSourceRule ID_SOURCE_RULE;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LogUtil.info("appactive-request-init");
        IdSourceRuleService idSourceRuleService = ClientRuleService.getIdSourceRuleService();
        if (idSourceRuleService == null){
            throw ExceptionFactory.makeFault("idSourceRuleService is null");
        }
        ID_SOURCE_RULE = idSourceRuleService.getIdSourceRule();
        if (ID_SOURCE_RULE == null || CollectionUtils.isEmpty(ID_SOURCE_RULE.getSourceList()) || StringUtils.isBlank(ID_SOURCE_RULE.getTokenKey())){
            throw ExceptionFactory.makeFault("idSourceRule is invalid");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        IdSourceEnum idSourceEnum = getRawRouterIdSuccess(ID_SOURCE_RULE, httpRequest);
        if (idSourceEnum != null){
            LogUtil.info(AppactiveConstant.PROJECT_NAME + "-request-doFilter-" + idSourceEnum + ":" + AppContextClient.getRouteId());
            chain.doFilter(request, response);
            clear();
            return;
        }
        // nothing
        chain.doFilter(request, response);
    }

    private void clear() {
        AppContextClient.clearUnitContext();
    }


    @Override
    public IdSourceEnum getRawRouterIdSuccess(IdSourceRule idSourceRule, HttpServletRequest request){
        List<IdSourceEnum> sourceList = idSourceRule.getSourceList();
        for (IdSourceEnum idSourceEnum : sourceList) {
            BiFunction<HttpServletRequest,String, String> func = STRATEGY_MAP.get(idSourceEnum);
            if (func == null){
                LogUtil.warn(AppactiveConstant.PROJECT_NAME + "-no-such-handler:"+idSourceEnum);
                continue;
            }
            String routeId = func.apply(request,idSourceRule.getTokenKey());
            if (check(routeId)){
                return idSourceEnum;
            }
        }
        return null;
    }

    private static Boolean check(String routeId){
        if (StringUtils.isBlank(routeId)) {
            return false;
        }
        AppContextClient.setUnitContext(routeId);
        return true;
    }

    @Override
    public void destroy() {
        LogUtil.info(AppactiveConstant.PROJECT_NAME+"-request-destroy");
    }

}
