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

package io.appactive.support.sys;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.appactive.java.api.base.constants.AppactiveConstant;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.log.LogUtil;

public class JvmPropertyUtil {


    public static String getJvmValue(String key) {
        return getValueFromJVM(key);
    }

    /**
     * 从 jvm 和 env 获得值，jvm 为第一优先级
     *
     * @param key as it is
     * @return value of the key
     */
    public static String getJvmAndEnvValue(String key) {
        String valueFromJVM = getValueFromJVM(key);
        if (StringUtils.isNotBlank(valueFromJVM)) {
            return valueFromJVM;
        }
        return getValueFromEnv(key);
    }

    public static String getEnvValue(String key) {
        return getValueFromEnv(key);
    }

    public static Boolean getJvmAndEnvBooleanValue(String key) {
        return getJvmAndEnvBooleanValue(key, null);
    }

    public static Boolean getJvmAndEnvBooleanValue(String key, Boolean defaultValue) {
        String jvmAndEnvValue = getJvmAndEnvValue(key);
        if (StringUtils.isBlank(jvmAndEnvValue)) {
            return defaultValue;
        }

        try {
            return Boolean.valueOf(jvmAndEnvValue);
        } catch (Exception e) {
            LogUtil.info("Boolean.valueOf({}) false,e:{},use default value:{}", jvmAndEnvValue, e.getMessage(),
                defaultValue);
            return defaultValue;
        }
    }


    public static String getLocalEnvDisasterFileName() {
        return LOCAL_ENV_DISASTER_FILE_NAME;
    }

    public static Boolean isTestCase() {return TEST_CASE_START;}

    public static String getAppName() {return APP_NAME;}


    private static Map<String, String> JVM_MEM_MAP = new ConcurrentHashMap<String, String>();
    private static Map<String, String> ENV_MEM_MAP = new ConcurrentHashMap<String, String>();

    /*****本地环境的容灾文件****/
    private static String LOCAL_ENV_DISASTER_FILE_NAME;

    /*******仅给测试写测试用例使用**********/
    private static Boolean TEST_CASE_START = false;

    private static String APP_NAME;

    static {
        initTestCase();
        initAppName();
    }

    private static void initAppName() {
        String appNameKey = AppactiveConstant.PROJECT_NAME.toLowerCase() + ".app.name";
        String property = System.getProperty(appNameKey);
        String projectName = System.getProperty("project.name");

        if (StringUtils.isNotBlank(property)) {
            // 第一优先级生效
            APP_NAME = StringUtils.trim(property);
            LogUtil.info(appNameKey+":" + APP_NAME);
            return;
        }
        if (StringUtils.isNotBlank(projectName)) {
            // 第2优先级生效
            APP_NAME = StringUtils.trim(projectName);
            LogUtil.info("[project.name]app.name:" + APP_NAME);
            return;
        }
        LogUtil.info("[no-jvm-property]app.name:" + APP_NAME);
    }

    private static void initTestCase() {
        String property = System.getProperty("test.case.start");
        if (StringUtils.isNotBlank(property)) {
            TEST_CASE_START = Boolean.valueOf(property);
        }
        LogUtil.info("test.case.start:" + TEST_CASE_START);
    }

    private static String getValueFromJVM(String key) {
        if (StringUtils.isEmpty(key)) {
            LogUtil.debug("getValueFromJVM by key[null] successful,return:null");
            return null;
        }
        String value = JVM_MEM_MAP.get(key);
        if (value == null) {
            // 内存无值， 设置值 放入内存
            value = System.getProperty(key, AppactiveConstant.EMPTY_STRING);
            JVM_MEM_MAP.put(key, value);
            LogUtil.debug("key[{}] not in mem,put it, value:{}", key, value);
        }

        if (value.equals(AppactiveConstant.EMPTY_STRING)) {
            // 无值
            LogUtil.debug("getValueFromJVM by key[{}],it is empty, return:null", key);
            return null;
        }
        LogUtil.debug("getValueFromJVM by key[{}],return:{}", key, value);
        return value;
    }

    private static String getValueFromEnv(String key) {
        if (StringUtils.isEmpty(key)) {
            LogUtil.debug("getValueFromEnv by key[null] successful,return:null");
            return null;
        }
        String value = ENV_MEM_MAP.get(key);
        if (value == null) {
            // 内存无值， 设置值 放入内存
            value = System.getenv(key);
            if (value == null) {
                value = AppactiveConstant.EMPTY_STRING;
            }
            ENV_MEM_MAP.put(key, value);
            LogUtil.debug("getValueFromEnv key[{}] not in mem,put it, value:{}", key, value);
        }

        if (value.equals(AppactiveConstant.EMPTY_STRING)) {
            // 无值
            LogUtil.debug("getValueFromEnv by key[{}],it is empty, return:null", key);
            return null;
        }
        LogUtil.debug("getValueFromEnv by key[{}],return:{}", key, value);
        return value;
    }

}
