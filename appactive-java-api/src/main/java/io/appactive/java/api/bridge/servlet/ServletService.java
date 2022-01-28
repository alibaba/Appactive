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

package io.appactive.java.api.bridge.servlet;

import io.appactive.java.api.rule.traffic.bo.IdSourceEnum;
import io.appactive.java.api.rule.traffic.bo.IdSourceRule;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 *
 */
public interface ServletService {
    /**
     * get routeId from header
     * @param request the request
     * @param tokenKey the header key
     * @return the value of the above key
     */
    static String getRouteIdFromHeader(@NotNull HttpServletRequest request,
                                       String tokenKey) {
        return request.getHeader(tokenKey);
    }

    /**
     * get routeId from cookie
     * @param request the request
     * @param tokenKey the cookie key
     * @return the value of the above key
     */
    static String getRouteIdFromCookie(@NotNull HttpServletRequest request,
                                       String tokenKey) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (name.equalsIgnoreCase(tokenKey)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * get routeId from params
     * @param request the request
     * @param tokenKey the query key
     * @return the value of the above key
     */
    static String getRouteIdFromParams(@NotNull HttpServletRequest request,
                                       String tokenKey) {
        return request.getParameter(tokenKey);
    }

    /**
     * set routerId according to IdSourceRule（ordered source） and request
     * @param IdSourceRule the IdSourceRule
     * @param request the request
     *
     * @return the source from which we get routerId
     *  or null when there is no required routerId in the request
     */
    IdSourceEnum getRawRouterIdSuccess(IdSourceRule IdSourceRule,
                                       @NotNull HttpServletRequest request);
}
