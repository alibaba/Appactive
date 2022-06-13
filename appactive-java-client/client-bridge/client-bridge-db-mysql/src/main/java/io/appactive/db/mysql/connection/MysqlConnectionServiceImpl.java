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

package io.appactive.db.mysql.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import io.appactive.db.mysql.connection.proxy.ConnectionProxy;
import io.appactive.db.mysql.constants.MysqlConstant;
import io.appactive.db.mysql.utils.DataScopeUtil;
import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.bridge.db.connection.MysqlConnectionService;
import io.appactive.java.api.bridge.db.constants.DataScope;
import io.appactive.java.api.bridge.db.sql.SQLProtectService;
import io.appactive.java.api.rule.TrafficMachineService;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.traffic.ForbiddenRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rule.ClientRuleService;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;

public class MysqlConnectionServiceImpl implements MysqlConnectionService, SQLProtectService {

    private static final Logger logger = LogUtil.getLogger();

    private final TrafficRouteRuleService trafficRouteRuleService = ClientRuleService.getTrafficRouteRuleService();
    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();
    private final TrafficMachineService trafficMachineService = new TrafficMachineService(trafficRouteRuleService,
        machineUnitRuleService);
    private final ForbiddenRuleService forbiddenRuleService = ClientRuleService.getForbiddenRuleService();


    private static final String INSTANCE_ID = "instance_id";

    private static final String DB_NAME = "db_name";
    private static final String PORT_NAME = "db_port";

    @Override
    public Connection getConnection(Connection connection, Properties info) throws SQLException {
        return new ConnectionProxy(connection, info, this);
    }

    @Override
    public void initDriverConnect(String url, Properties property) {
        logger.info("driver:ConnectionServiceImpl::initDriverConnect start. url: {}, property: {}", url, property);
        DataScope dataScope = DataScopeUtil.getDataScopeFromUrl(url);
        if (dataScope == null) {
            logger.info("ConnectionServiceImpl::initDriverConnect dataScope is empty. url: {}, property: {}", url, property);
            return;
        }
        setDataScopeToProperty(property, dataScope);
    }


    @Override
    public void sqlProtect(String sql, Properties property)throws SQLException {
        DataScope dataScope = getDataScopeFromProperty(property);
        if (dataScope == null) {
            return;
        }
        if (!this.isInAppActive(property)) {
            return;
        }
        String routeId = AppContextClient.getRouteId();
        if (StringUtils.isBlank(routeId)){
            throw new SQLException(MysqlConstant.ERROR_ROUTE_FLOW_ROUTER_NOT_HAVE_ROUTER_ID);
        }
        forbiddenProtect(routeId);
        dailyUnitWriteProtect(routeId);
    }


    private void dailyUnitWriteProtect(String routeId) throws SQLException {
        boolean inCurrentUnit = trafficMachineService.isInCurrentUnit(routeId);
        if (inCurrentUnit){
            return;
        }
        String currentUnit = machineUnitRuleService.getCurrentUnit();
        String trafficUnit = trafficRouteRuleService.getUnitByRouteId(routeId);
        throw new SQLException("machine:"+currentUnit+",traffic:"+trafficUnit+",not equals");
    }

    private void forbiddenProtect(String routeId) throws SQLException {
        boolean forbidden = forbiddenRuleService.isRouteIdForbidden(routeId);
        if (forbidden){
            String currentUnit = machineUnitRuleService.getCurrentUnit();
            throw new SQLException("machine:"+currentUnit+" forbids routerId "+routeId);
        }
    }

    @Override
    public boolean isInAppActive(Properties property) {
        DataScope dataScope = getDataScopeFromProperty(property);
        boolean dataScopeExist = ClientRuleService.getDataScopeRuleService().isDataScopeExist(dataScope);
        return dataScopeExist;
    }


    private void setDataScopeToProperty(Properties property, DataScope dataScope) {
        property.put(INSTANCE_ID, dataScope.getInstanceId());
        property.put(DB_NAME, dataScope.getDatabaseName());
        if (StringUtils.isNotBlank(dataScope.getPort())) {
            property.put(PORT_NAME, dataScope.getPort());
        }
    }

    private DataScope getDataScopeFromProperty(Properties property) {
        if (property == null) {
            return null;
        }
        String instanceId = property.getProperty(INSTANCE_ID);
        String dbName = property.getProperty(DB_NAME);
        String port = property.getProperty(PORT_NAME);
        if (StringUtils.isBlank(instanceId) || StringUtils.isBlank(dbName)) {
            return null;
        }
        return new DataScope(instanceId, dbName, port);
    }



}
