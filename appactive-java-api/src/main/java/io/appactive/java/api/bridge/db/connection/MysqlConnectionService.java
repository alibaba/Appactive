package io.appactive.java.api.bridge.db.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import io.appactive.java.api.base.extension.SPI;

@SPI
public interface MysqlConnectionService {

    Connection getConnection(Connection connection, Properties info) throws SQLException;

    void initDriverConnect(String url, Properties info);

    boolean isInAppActive(Properties info);
}
