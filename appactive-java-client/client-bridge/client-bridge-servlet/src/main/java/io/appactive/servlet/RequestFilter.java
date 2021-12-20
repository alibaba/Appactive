package io.appactive.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.appactive.java.api.base.constants.AppactiveConstant;
import io.appactive.java.api.bridge.servlet.ServletService;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.log.LogUtil;

/**
 */
public class RequestFilter implements Filter, ServletService {

    private static final String REQUEST_ROUTER_ID_KEY = "r_id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LogUtil.info("appactive-request-init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        // header
        if (initFromHeaderSuccess(httpRequest)) {
            LogUtil.info(AppactiveConstant.PROJECT_NAME+"-request-doFilter-header:"+ AppContextClient.getRouteId().toString());
            chain.doFilter(request, response);
            clear();
            return;
        }
        if (initFromCookieSuccess(httpRequest)) {
            LogUtil.info(AppactiveConstant.PROJECT_NAME+"-request-doFilter-cookie:"+ AppContextClient.getRouteId().toString());
            chain.doFilter(request, response);
            clear();
            return;
        }
        if (initFromParamsSuccess(httpRequest)) {
            LogUtil.info(AppactiveConstant.PROJECT_NAME+"-request-doFilter-params:"+ AppContextClient.getRouteId().toString());
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

    private boolean initFromParamsSuccess(HttpServletRequest request) {
        String routeId = getRouteIdFromParams(request);
        if (StringUtils.isBlank(routeId)) {
            return false;
        }
        AppContextClient.setUnitContext(routeId);
        return true;

    }

    private boolean initFromCookieSuccess(HttpServletRequest request) {
        String routeId = getRouteIdFromCookie(request);
        if (StringUtils.isBlank(routeId)) {
            return false;
        }
        AppContextClient.setUnitContext(routeId);
        return true;
    }

    private boolean initFromHeaderSuccess(HttpServletRequest httpRequest) {
        String routeId = getRouteIdFromHeader(httpRequest);
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

    @Override
    public String getRouteIdFromHeader(HttpServletRequest request) {
        if (request == null){
            return null;
        }
        return request.getHeader(REQUEST_ROUTER_ID_KEY);
    }

    @Override
    public String getRouteIdFromCookie(HttpServletRequest request) {
        if (request == null){
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (name.equalsIgnoreCase(REQUEST_ROUTER_ID_KEY)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public String getRouteIdFromParams(HttpServletRequest request) {
        if (request == null){
            return null;
        }
        return request.getParameter(REQUEST_ROUTER_ID_KEY);
    }
}
