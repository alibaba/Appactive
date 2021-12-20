package io.appactive.java.api.bridge.db.sql;

import java.sql.SQLException;
import java.util.Properties;


public interface SQLProtectService {

    void sqlProtect(String sql, Properties info) throws SQLException;
}
