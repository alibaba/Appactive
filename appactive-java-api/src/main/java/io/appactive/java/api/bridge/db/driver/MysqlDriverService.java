package io.appactive.java.api.bridge.db.driver;

import java.util.Properties;

public interface MysqlDriverService {

    /**
     * if appactive,check current mysql is support
     * @param info
     */
    void check(Properties info);

    java.sql.Driver getProxyDriver();

}
