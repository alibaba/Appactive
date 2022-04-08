package io.appactive.channel.nacos;

import io.appactive.channel.PathUtil;
import io.appactive.channel.file.FilePathUtil;
import io.appactive.channel.file.RulePropertyConstant;
import io.appactive.support.log.LogUtil;
import io.appactive.support.sys.JvmPropertyUtil;

import static io.appactive.channel.file.RulePropertyConstant.GROUP_ID;

public class NacosPathUtil implements PathUtil {

    private static volatile NacosPathUtil instance;

    public static NacosPathUtil getInstance() {
        if (instance == null) {
            synchronized(FilePathUtil.class) {
                if (instance == null) {
                    instance = new NacosPathUtil();
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

    public static String getGroupId() {
        return GROUP_ID;
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

    }

}