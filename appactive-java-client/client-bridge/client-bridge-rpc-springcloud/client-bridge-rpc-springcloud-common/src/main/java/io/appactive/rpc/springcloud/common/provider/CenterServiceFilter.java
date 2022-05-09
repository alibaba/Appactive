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

package io.appactive.rpc.springcloud.common.provider;

import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.bridge.servlet.ServletService;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rpc.springcloud.common.Constants;
import io.appactive.rule.ClientRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * only for center service
 *
 * @author mageekchiu
 */
public class CenterServiceFilter implements Filter{

    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException{
        if (!machineUnitRuleService.isCenterUnit()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "this is not center machine:" + machineUnitRuleService.getCurrentUnit());
        }
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String routerId = ServletService.getRouteIdFromHeader(httpRequest, Constants.ROUTER_ID_HEADER_KEY);
        if (StringUtils.isNotBlank(routerId)){
            AppContextClient.setUnitContext(routerId);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
