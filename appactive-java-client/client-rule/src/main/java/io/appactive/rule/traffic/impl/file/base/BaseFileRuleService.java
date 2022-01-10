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

package io.appactive.rule.traffic.impl.file.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import io.appactive.channel.file.FileReadDataSource;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rule.base.file.FileConstant;
import io.appactive.rule.traffic.bo.item.RuleItemType;
import io.appactive.rule.traffic.bo.UnitMappingRuleBO;

public class BaseFileRuleService {

    protected FileReadDataSource<UnitMappingRuleBO> getUnitMappingRuleReadDataSource(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw ExceptionFactory.makeFault("filePath is empty");
        }
        ConverterInterface<String, UnitMappingRuleBO> ruleConverterInterface = source -> JSON.parseObject(source,
            new TypeReference<UnitMappingRuleBO>() {});
        FileReadDataSource<UnitMappingRuleBO> fileReadDataSource = new FileReadDataSource<>(filePath,
            FileConstant.DEFAULT_CHARSET, FileConstant.DEFAULT_BUF_SIZE, ruleConverterInterface);
        return fileReadDataSource;
    }

    protected boolean checkRuleRight(UnitMappingRuleBO unitMappingRule) {
        if (unitMappingRule == null) {
            return true;
        }
        String itemType = unitMappingRule.getItemType();
        if (StringUtils.isBlank(itemType)) {
            return false;
        }
        RuleItemType ruleItemType = RuleItemType.get(itemType);
        if (ruleItemType == null) {
            return false;
        }
        return true;
    }
}
