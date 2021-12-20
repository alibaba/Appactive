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

    private static final String INSTANCE_ID = "instance_id";

    private static final String DB_NAME = "db_name";
    private static final String PORT_NAME = "db_port";

    @Override
    public Connection getConnection(Connection connection, Properties info) throws SQLException {
        return new ConnectionProxy(connection, info, this);
    }

    @Override
    public void initDriverConnect(String url, Properties property) {
        logger.info("driver:ConnectionServiceImpl::initDriverConnect start,url:{}.property:{}", url, property);
        DataScope dataScope = DataScopeUtil.getDataScopeFromUrl(url);
        if (dataScope == null) {
            // url 无多活标识，默认不建立监听
            logger.info("ConnectionServiceImpl::initDriverConnect dataScope is empty. url:{},property:{}", url, property);
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
        dailyUnitWriteProtect();
    }


    private void dailyUnitWriteProtect() throws SQLException {
        String routeId = AppContextClient.getRouteId();
        if (StringUtils.isBlank(routeId)){
            throw new SQLException(MysqlConstant.ERROR_ROUTE_FLOW_ROUTER_NOT_HAVE_ROUTER_ID);
        }
        boolean inCurrentUnit = trafficMachineService.isInCurrentUnit(routeId);
        if (inCurrentUnit){
            return;
        }
        String currentUnit = machineUnitRuleService.getCurrentUnit();
        String trafficUnit = trafficRouteRuleService.getUnitByRouteId(routeId);
        throw new SQLException("machine:"+currentUnit+",traffic:"+trafficUnit+",not equals");
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
        // 若 url 无两个instanceId与dbName参数，则表示非多活逻辑，不走 多活逻辑
        if (StringUtils.isBlank(instanceId) || StringUtils.isBlank(dbName)) {
            return null;
        }
        return new DataScope(instanceId, dbName, port);
    }



}
