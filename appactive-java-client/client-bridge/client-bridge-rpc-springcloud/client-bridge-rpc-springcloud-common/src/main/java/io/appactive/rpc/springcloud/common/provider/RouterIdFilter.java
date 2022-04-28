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
import io.appactive.java.api.base.constants.AppactiveConstant;
import io.appactive.java.api.bridge.servlet.ServletService;
import io.appactive.rpc.springcloud.common.Constants;
import io.appactive.support.log.LogUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * only for unit service
 *
 * @author mageekchiu
 */
public class RouterIdFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String routerId = ServletService.getRouteIdFromHeader(httpRequest, Constants.ROUTER_ID_HEADER_KEY);
        AppContextClient.setUnitContext(routerId);
        LogUtil.info(AppactiveConstant.PROJECT_NAME + "-routerIdFilter-doFilter-header:" + AppContextClient.getRouteId());
        chain.doFilter(request, response);
        clear();
    }

    @Override
    public void destroy() {

    }

    private void clear() {
        AppContextClient.clearUnitContext();
    }


}
