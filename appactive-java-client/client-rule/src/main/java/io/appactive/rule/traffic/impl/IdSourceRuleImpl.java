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

package io.appactive.rule.traffic.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.appactive.channel.ClientChannelService;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.channel.ConfigReadDataSource;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.rule.RuleTypeEnum;
import io.appactive.java.api.rule.traffic.IdSourceRuleService;
import io.appactive.java.api.rule.traffic.bo.IdSourceEnum;
import io.appactive.java.api.rule.traffic.bo.IdSourceRule;
import io.appactive.rule.ClientRuleService;
import io.appactive.support.log.LogUtil;

import java.util.LinkedList;
import java.util.List;

public class IdSourceRuleImpl implements IdSourceRuleService {

    private IdSourceRule idSourceRule;

    public IdSourceRuleImpl() {
        initFromUri(ClientRuleService.getDefaultUri(RuleTypeEnum.idSourceRulePath));
    }

    public IdSourceRuleImpl(String uri) {
        initFromUri(uri);
    }

    @Override
    public IdSourceRule getIdSourceRule() {
        return idSourceRule;
    }

    private void initFromUri(String uri) {

        ConverterInterface<String, IdSourceRule> converterInterface = (source) -> {
            JSONObject jo = JSON.parseObject(source);
            String tokenKey = jo.getString("tokenKey");
            List<IdSourceEnum> list = new LinkedList<>();
            for (String s : jo.getString("source").split(",")) {
                list.add(IdSourceEnum.valueOf(s));
            }
            IdSourceRule idSourceRule = new IdSourceRule();
            idSourceRule.setTokenKey(tokenKey);
            idSourceRule.setSourceList(list);
            return idSourceRule;
        };
        ConfigReadDataSource<IdSourceRule> readDataSource =  ClientChannelService.getConfigReadDataSource(uri, converterInterface);
        try {
            idSourceRule = readDataSource.read();
        } catch (Exception e) {
            String msg = "initFromUri exception:" + e.getMessage();
            LogUtil.error(msg,e);
            throw ExceptionFactory.makeFault(msg);
        }
    }


}
