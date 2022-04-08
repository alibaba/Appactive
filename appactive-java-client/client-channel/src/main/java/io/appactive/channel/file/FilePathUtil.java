package io.appactive.channel.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.appactive.channel.PathUtil;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.log.LogUtil;
import io.appactive.support.sys.JvmPropertyUtil;

import java.util.Map;

public class FilePathUtil implements PathUtil {

    private static final String ALL_RULE_DEFINE_PATH = "/home/admin/appactive/path-address";

    private static volatile FilePathUtil instance;

    public FilePathUtil() {
        try {
            initPathValue();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(),e);
        }
    }

    public static FilePathUtil getInstance() {
        if (instance == null) {
            synchronized(FilePathUtil.class) {
                if (instance == null) {
                    instance = new FilePathUtil();
                }
            }
        }
        return instance;
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


    private void initPathValue() throws Exception {
        // 1. from system
        initFromSys();

        // 2. from file
        initFromFile();
    }


    private void initFromSys() {
        machineRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".machineRulePath");
        dataScopeRuleDirectoryPath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".dataScopeRuleDirectoryPath");
        forbiddenRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".forbiddenRulePath");
        trafficRouteRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".trafficRulePath");
        transformerRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".transformerRulePath");
        idSourceRulePath = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.PROPERTY_HEADER+".idSourceRulePath");
    }

    private void initFromFile() throws Exception {
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
        if (idSourceRulePath == null) {
            idSourceRulePath = pathMap.get(RulePropertyConstant.PROPERTY_HEADER + ".idSourceRulePath");
        }
    }
}

