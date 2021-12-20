package io.appactive.db.mysql.constants;

public interface MysqlConstant {
    String INSTANCE_ID_KEY = "activeInstanceId";

    String DB_NAME_KEY = "activeDbName";

    String PORT_NAME_KEY = "activePort";

    String ERROR_ROUTE_FLOW_ROUTER_NOT_HAVE_ROUTER_ID =
        "the route flow routerId is null is forbidden by db unit.";
}
