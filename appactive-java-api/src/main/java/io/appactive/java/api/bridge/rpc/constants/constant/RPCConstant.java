package io.appactive.java.api.bridge.rpc.constants.constant;


public interface RPCConstant {



    /**
     * auto by machine
     * url：unit label
     */
    String URL_UNIT_LABEL_KEY = "ut";

    /**
     * manual
     * url: resource active
     * for example: CENTER_RESOURCE_TYPE/UNIT_RESOURCE_TYPE/NORMAL_RESOURCE_TYPE
     */
    String URL_RESOURCE_ACTIVE_LABEL_KEY = "ra";

    String URL_RESOURCE_ACTIVE_LABEL = "rsActive";

    /**
     * manual(optional)
     * url: route index key
     * get route Id by params, if notExit, use thread
     */
    String URL_ROUTE_INDEX_KEY = "ri";
    String URL_ROUTE_INDEX = "routeIndex";

    String CONSUMER_REMOTE_ROUTE_ID_KEY = "_unit_rpc_route_id";
}
