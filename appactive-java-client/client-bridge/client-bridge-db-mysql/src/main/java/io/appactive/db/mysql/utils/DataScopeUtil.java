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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.appactive.db.mysql.constants.MysqlConstant;
import io.appactive.db.mysql.utils.JDBCUrlSplitterUtil.JdbcUrlSpiltResult;
import io.appactive.java.api.bridge.db.constants.DataScope;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;

public class DataScopeUtil {

    private static Logger logger = LogUtil.getLogger();

    public static DataScope getDataScopeFromUrl(String url){
        Map<String, String> connPropMap = getConnPropMap(url);
        // 推荐的声明方式，但需要业务改
        DataScope dataScope = initDataScope(MysqlConstant.INSTANCE_ID_KEY, MysqlConstant.DB_NAME_KEY,MysqlConstant.PORT_NAME_KEY, connPropMap);
        if (dataScope != null) {
            return dataScope;
        }
        // 兼容直接从jdbc_url方式获取
        return initDataScope(url);
    }


    private static DataScope initDataScope(String instanceId, String dbName, String portName,
                                           Map<String, String> connPropMap) {
        instanceId = instanceId.toLowerCase();
        dbName = dbName.toLowerCase();
        portName = portName.toLowerCase();
        if (connPropMap.containsKey(instanceId) && connPropMap.containsKey(dbName)) {
            String instanceValue = connPropMap.get(instanceId);
            String dbValue = connPropMap.get(dbName);
            String portValue = connPropMap.get(portName);
            if (StringUtils.isBlank(instanceValue)) {
                return null;
            }
            if (StringUtils.isBlank(dbValue)) {
                return null;
            }
            if (StringUtils.isBlank(portValue)){
                return new DataScope(instanceValue.toLowerCase(), dbValue.toLowerCase(), null);
            }

            // 默认实例名称和库名称大小写不敏感
            return new DataScope(instanceValue.toLowerCase(), dbValue.toLowerCase(), portValue.toLowerCase());
        }
        return null;
    }

    private static DataScope initDataScope(String url) {
        JdbcUrlSpiltResult spiltResult = null;
        try {
            spiltResult = JDBCUrlSplitterUtil.spilt(url);
        } catch (Exception e) {
            logger.error("MysqlDriverProcessInnerService::initDataScope fail by spiltResult parse fail. url:{}", url, e);
        }
        if (spiltResult == null) {
            logger.error("MysqlDriverProcessInnerService::initDataScope fail by spiltResult parse null. url:{}", url);
            return null;
        }
        String host = spiltResult.getHost();
        String dbName = spiltResult.getDbName();
        String port = spiltResult.getPort();
        if (StringUtils.isBlank(host) || StringUtils.isBlank(dbName) || StringUtils.isBlank(port)) {
            logger.error("MysqlDriverProcessInnerService::initDataScope fail by spiltResult parse fail."
                + "url:{}, spiltResult:{}", url, spiltResult);
            return null;
        }
        // 默认实例名称和库名称大小写和port不敏感
        return new DataScope(host.toLowerCase(), dbName.toLowerCase(), port.toLowerCase());
    }

    private static Map<String, String> getConnPropMap(String url) {
        Map<String, String> connPropMap = new HashMap<String, String>();
        int idxOfQuestionMark = StringUtils.indexOf(url, '?');
        if (idxOfQuestionMark == StringUtils.INDEX_NOT_FOUND) {
            return connPropMap;
        }
        String connPropStr = StringUtils.substring(url, idxOfQuestionMark + 1, url.length());
        if (StringUtils.isEmpty(connPropStr)) {
            return connPropMap;
        }
        connPropStr = connPropStr.trim();
        String[] connPropArr = connPropStr.split("&");
        for (int i = 0; i < connPropArr.length; i++) {
            String connPropVal = connPropArr[i];
            String[] keyAndVal = connPropVal.split("=");
            String key = keyAndVal[0];
            String val = keyAndVal[1];
            // 忽略大小写，先转小写判断
            connPropMap.put(key.toLowerCase(), val);
        }
        return connPropMap;
    }

}
