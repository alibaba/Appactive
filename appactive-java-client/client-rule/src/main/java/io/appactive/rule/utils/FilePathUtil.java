package io.appactive.rule.utils;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import io.appactive.channel.file.FileReadDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rule.base.file.FileConstant;
import io.appactive.rule.base.property.RulePropertyConstant;
import io.appactive.support.log.LogUtil;
import io.appactive.support.sys.JvmPropertyUtil;

public class FilePathUtil {

    private static final String ALL_RULE_DEFINE_PATH = "/home/admin/appactive/path-address";

    private static String machineRulePath;
    private static String dataScopeRuleDirectoryPath;
    private static String forbiddenRulePath;
    private static String trafficRouteRulePath;
    private static String transformerRulePath;

    public static String getMachineRulePath() {
        return machineRulePath;
    }

    public static String getDataScopeRuleDirectoryPath() {
        return dataScopeRuleDirectoryPath;
    }

    public static String getForbiddenRulePath() {
        return forbiddenRulePath;
    }

    public static String getTrafficRouteRulePath() {
        return trafficRouteRulePath;
    }

    public static String getTransformerRulePath() {
        return transformerRulePath;
    }

    static {
        try {
            initPathValue();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(),e);
        }
    }

    private static void initPathValue() throws Exception {
        // 1. from system
        initFromSys();

        // 2. from file
        initFromFile();
    }


    private static void initFromSys() {
        machineRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".machineRulePath");
        dataScopeRuleDirectoryPath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".dataScopeRuleDirectoryPath");
        forbiddenRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".forbiddenRulePath");
        trafficRouteRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".trafficRulePath");
        transformerRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".transformerRulePath");
    }

    private static void initFromFile() throws Exception {
        String filePathName = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".path");
        if (StringUtils.isBlank(filePathName)){
            filePathName = ALL_RULE_DEFINE_PATH;
        }

        ConverterInterface<String, Map<String,String>> converterInterface = source -> JSON.parseObject(source,
            new TypeReference<Map<String,String>>() {});
        FileReadDataSource<Map<String,String>> fileReadDataSource = new FileReadDataSource<>(filePathName,
            FileConstant.DEFAULT_CHARSET, FileConstant.DEFAULT_BUF_SIZE, converterInterface);
        Map<String, String> pathMap = fileReadDataSource.read();
        if (machineRulePath == null) {
            machineRulePath = pathMap.get(RulePropertyConstant.PROPERTY_HEADER + ".machineRulePath");
        }
        if (dataScopeRuleDirectoryPath == null) {
            dataScopeRuleDirectoryPath = pathMap.get(RulePropertyConstant.PROPERTY_HEADER + ".dataScopeRuleDirectoryPath");
        }
        if (forbiddenRulePath == null) {
            forbiddenRulePath = pathMap.get(RulePropertyConstant.PROPERTY_HEADER + ".forbiddenRulePath");
        }
        if (trafficRouteRulePath == null) {
            trafficRouteRulePath = pathMap.get(RulePropertyConstant.PROPERTY_HEADER + ".trafficRulePath");
        }
        if (transformerRulePath == null) {
            transformerRulePath = pathMap.get(RulePropertyConstant.PROPERTY_HEADER + ".transformerRulePath");
        }
    }


}
