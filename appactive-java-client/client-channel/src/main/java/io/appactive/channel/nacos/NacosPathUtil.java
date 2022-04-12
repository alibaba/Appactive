package io.appactive.channel.nacos;

import io.appactive.channel.PathUtil;
import io.appactive.channel.file.RulePropertyConstant;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.log.LogUtil;
import io.appactive.support.sys.JvmPropertyUtil;

import java.util.Properties;

public class NacosPathUtil implements PathUtil {

    private static volatile NacosPathUtil instance;

    public static NacosPathUtil getInstance() {
        if (instance == null) {
            synchronized(NacosPathUtil.class) {
                if (instance == null) {
                    instance = new NacosPathUtil();
                    LogUtil.warn("instance: {}",instance);
                }
            }
        }
        return instance;
    }

    public NacosPathUtil() {
        try {
            initPathValue();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(),e);
        }
    }

    private String machineRulePath;
    private String dataScopeRuleDirectoryPath;
    private String forbiddenRulePath;
    private String trafficRouteRulePath;
    private String transformerRulePath;
    private String idSourceRulePath;
    private String configServerAddress;

    private final Properties extras = new Properties();
    private final Properties auths = new Properties();

    @Override
    public String getMachineRulePath() {
        return machineRulePath;
    }

    @Override
    public String getDataScopeRuleDirectoryPath() {
        return dataScopeRuleDirectoryPath;
    }

    @Override
    public String getForbiddenRulePath() {
        return forbiddenRulePath;
    }

    @Override
    public String getTrafficRouteRulePath() {
        return trafficRouteRulePath;
    }

    @Override
    public String getTransformerRulePath() {
        return transformerRulePath;
    }

    @Override
    public String getIdSourceRulePath() {
        return idSourceRulePath;
    }

    @Override
    public String getConfigServerAddress(){
        return configServerAddress;
    }

    @Override
    public Properties getAuth() {
        return auths;
    }

    @Override
    public Properties getExtras() {
        return extras;
    }

    private void initPathValue() throws Exception {
        // 1. from system
        initFromSys();
    }

    private void initFromSys() {
        String key, value;

        key = RulePropertyConstant.DATA_ID_HEADER+".machineRulePath";
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        machineRulePath = value == null ? key : value;

        key = RulePropertyConstant.DATA_ID_HEADER+".dataScopeRuleDirectoryPath";
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        dataScopeRuleDirectoryPath = value == null ? key : value;

        key = RulePropertyConstant.DATA_ID_HEADER+".forbiddenRulePath";
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        forbiddenRulePath = value == null ? key : value;

        key = RulePropertyConstant.DATA_ID_HEADER+".trafficRouteRulePath";
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        trafficRouteRulePath = value == null ? key : value;

        key = RulePropertyConstant.DATA_ID_HEADER+".transformerRulePath";
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        transformerRulePath = value == null ? key : value;

        key = RulePropertyConstant.DATA_ID_HEADER+".idSourceRulePath";
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        idSourceRulePath = value == null ? key : value;

        key = RulePropertyConstant.PROPERTY_HEADER + ".configServerAddress";
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        configServerAddress = value == null ? RulePropertyConstant.LOCAL_NACOS : value;


        key = RulePropertyConstant.GROUP_ID;
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        value = value == null ? key : value;
        extras.put(key, value);

        key = RulePropertyConstant.NAMESPACE_ID;
        value = JvmPropertyUtil.getJvmAndEnvValue(key);
        String namespaceId = "";
        if (StringUtils.isNotBlank(value)){
            namespaceId = value;
        }
        extras.put(key, namespaceId);

    }

    @Override
    public String toString() {
        return "NacosPathUtil{" +
                "machineRulePath='" + machineRulePath + '\'' +
                ", dataScopeRuleDirectoryPath='" + dataScopeRuleDirectoryPath + '\'' +
                ", forbiddenRulePath='" + forbiddenRulePath + '\'' +
                ", trafficRouteRulePath='" + trafficRouteRulePath + '\'' +
                ", transformerRulePath='" + transformerRulePath + '\'' +
                ", idSourceRulePath='" + idSourceRulePath + '\'' +
                ", configServerAddress='" + configServerAddress + '\'' +
                ", extras=" + extras +
                ", auths=" + auths +
                '}';
    }
}