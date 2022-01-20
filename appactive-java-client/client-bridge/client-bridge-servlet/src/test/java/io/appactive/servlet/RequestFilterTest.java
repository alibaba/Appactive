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

import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.rule.traffic.bo.IdSourceEnum;
import io.appactive.java.api.rule.traffic.bo.IdSourceRule;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class RequestFilterTest {

    HttpServletRequest request;

    @Test
    public void idSourceRule(){
        RequestFilter requestFilter = new RequestFilter();

        IdSourceRule idSourceRule = new IdSourceRule(){{
            setTokenKey("r_id");
            setSourceList(new LinkedList<IdSourceEnum>(){{
                add(IdSourceEnum.arg);
                add(IdSourceEnum.header);
                add(IdSourceEnum.cookie);
            }});
        }};

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("r_id")).thenReturn("1");
        requestFilter.getRawRouterIdSuccess(idSourceRule,request);
        Assert.assertEquals(AppContextClient.getRouteId(),"1");

        request = mock(HttpServletRequest.class);
        when(request.getHeader("r_id")).thenReturn("2");
        requestFilter.getRawRouterIdSuccess(idSourceRule,request);
        Assert.assertEquals(AppContextClient.getRouteId(),"2");

        request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("r_id","3")});
        requestFilter.getRawRouterIdSuccess(idSourceRule,request);
        Assert.assertEquals(AppContextClient.getRouteId(),"3");

        request = mock(HttpServletRequest.class);
        when(request.getParameter("r_id")).thenReturn("1");
        when(request.getHeader("r_id")).thenReturn("2");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("r_id","3")});
        requestFilter.getRawRouterIdSuccess(idSourceRule,request);
        Assert.assertEquals(AppContextClient.getRouteId(),"1");

        request = mock(HttpServletRequest.class);
        when(request.getHeader("r_id")).thenReturn("2");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("r_id","3")});
        requestFilter.getRawRouterIdSuccess(idSourceRule,request);
        Assert.assertEquals(AppContextClient.getRouteId(),"2");

        request = mock(HttpServletRequest.class);
        when(request.getHeader("rid")).thenReturn("2");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("rid","3")});
        Assert.assertNull(requestFilter.getRawRouterIdSuccess(idSourceRule,request));
    }
}
