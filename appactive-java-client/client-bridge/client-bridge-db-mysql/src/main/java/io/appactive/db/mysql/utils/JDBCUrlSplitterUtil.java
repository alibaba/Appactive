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

package io.appactive.db.mysql.utils;


import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;

public class JDBCUrlSplitterUtil {

    private static final Logger logger = LogUtil.getLogger();

    public static JdbcUrlSpiltResult spilt(String jdbcUrl) {
        JdbcUrlSpiltResult result = new JdbcUrlSpiltResult();
        int pos, pos1, pos2;
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:")
            || (pos1 = jdbcUrl.indexOf(':', 5)) == -1) {
            return null;
        }
        if (jdbcUrl.contains("?")) {
            jdbcUrl = jdbcUrl.substring(0, jdbcUrl.indexOf("?"));
        }

        if (jdbcUrl.contains(";")) {
            jdbcUrl = jdbcUrl.substring(0, jdbcUrl.indexOf(";"));
        }
        String connUri;
        result.setDriverName(jdbcUrl.substring(5, pos1));
        if ((pos2 = jdbcUrl.indexOf(';', pos1)) == -1) {
            connUri = jdbcUrl.substring(pos1 + 1);
        } else {
            connUri = jdbcUrl.substring(pos1 + 1, pos2);
            result.setParams(jdbcUrl.substring(pos2 + 1));
        }

        if (connUri.startsWith("//")) {
            if ((pos = connUri.indexOf('/', 2)) != -1) {
                result.setHost(connUri.substring(2, pos));
                result.setDbName(connUri.substring(pos + 1));
                if ((pos = result.getHost().indexOf(':')) != -1) {
                    result.setPort(result.getHost().substring(pos + 1));
                    result.setHost(result.getHost().substring(0, pos));
                }
            }
        } else {
            result.setDbName(connUri);
        }
        return result;
    }

    public static class JdbcUrlSpiltResult {

        private String driverName;

        private String host;

        private String port;

        private String dbName;

        private String params;

        public String getDriverName() {
            return driverName;
        }

        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        @Override
        public String toString() {
            return "JdbcUrlSpiltResult{" +
                "driverName='" + driverName + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", dbName='" + dbName + '\'' +
                ", params='" + params + '\'' +
                '}';
        }

        public static void main(String[] args) {
            logger.info("JdbcUrlSpiltResult {}",spilt("jdbc:xugu://127.0.0.1:5138/TEST"));
        }

    }
}
