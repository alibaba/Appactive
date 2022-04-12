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

package io.appactive.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.appactive.channel.file.FileConstant;
import io.appactive.channel.file.FilePathUtil;
import io.appactive.channel.file.FileReadDataSource;
import io.appactive.channel.file.RulePropertyConstant;
import io.appactive.channel.nacos.NacosPathUtil;
import io.appactive.channel.nacos.NacosReadDataSource;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.channel.ChannelTypeEnum;
import io.appactive.java.api.channel.ConfigReadDataSource;
import io.appactive.java.api.channel.ConfigWriteDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.sys.JvmPropertyUtil;

import java.util.Properties;

/**
 * 选择合适的channel
 */
public class ClientChannelService {

    private static final ChannelTypeEnum CHANNEL_TYPE_ENUM;

    static {
        String channelType = JvmPropertyUtil.getJvmAndEnvValue(RulePropertyConstant.CHANNEL_TYPE_ENUM);
        if (StringUtils.isBlank(channelType)){
            CHANNEL_TYPE_ENUM = ChannelTypeEnum.FILE;
        }else {
            CHANNEL_TYPE_ENUM = ChannelTypeEnum.valueOf(channelType);
        }
    }

    public static <T> ConfigReadDataSource<T> getConfigReadDataSource(String uri) {
        ConverterInterface<String, T> ruleConverterInterface = (source) -> {
            try {
                T bo = JSON.parseObject(source,new TypeReference<T>() {});
                return bo;
            }catch (Exception e){
                throw ExceptionFactory.makeFault("unsupported content:{}", source);
            }
        };
        return getConfigReadDataSource(uri, ruleConverterInterface);
    }

    public static <T> ConfigReadDataSource<T> getConfigReadDataSource(String uri, ConverterInterface<String, T> ruleConverterInterface) {
        if (StringUtils.isBlank(uri)) {
            throw ExceptionFactory.makeFault("uri is empty");
        }

        ConfigReadDataSource<T> configReadDataSource;
        switch (CHANNEL_TYPE_ENUM){
            case FILE:
                configReadDataSource = new FileReadDataSource<>(uri,
                        FileConstant.DEFAULT_CHARSET, FileConstant.DEFAULT_BUF_SIZE,
                        ruleConverterInterface);
                break;
            case NACOS:
                PathUtil pathUtil = getPathUtil();
                Properties extra = pathUtil.getExtras();
                configReadDataSource = new NacosReadDataSource<>(pathUtil.getConfigServerAddress(), uri,
                        extra.getProperty(RulePropertyConstant.GROUP_ID), extra.getProperty(RulePropertyConstant.NAMESPACE_ID),
                        ruleConverterInterface);
                break;
            default:
                throw ExceptionFactory.makeFault("unsupported channel:{}", CHANNEL_TYPE_ENUM.name());
        }
        return configReadDataSource;
    }

    public static <T>  ConfigWriteDataSource<T> getConfigWriteDataSource(String uri, T clazz) {
        return null;
    }

    public static PathUtil getPathUtil(){
        switch (CHANNEL_TYPE_ENUM){
            case NACOS:
                return NacosPathUtil.getInstance();
            case FILE:
                return FilePathUtil.getInstance();
            default:
                throw ExceptionFactory.makeFault("unsupported channel:{}", CHANNEL_TYPE_ENUM.name());
        }
    }

    public static String getSubKeySplit(){
        switch (CHANNEL_TYPE_ENUM){
            case NACOS:
                return "_";
            case FILE:
                return "/";
            default:
                throw ExceptionFactory.makeFault("unsupported channel:{}", CHANNEL_TYPE_ENUM.name());
        }
    }

}
