package io.appactive.java.api.bridge.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface ServletService {
    /**
     * get routeId from header
     */
    String getRouteIdFromHeader(HttpServletRequest request);

    /**
     * get routeId from cookie
     */
    String getRouteIdFromCookie(HttpServletRequest request);

    /**
     * get routeId from params
     */
    String getRouteIdFromParams(HttpServletRequest request);
}
