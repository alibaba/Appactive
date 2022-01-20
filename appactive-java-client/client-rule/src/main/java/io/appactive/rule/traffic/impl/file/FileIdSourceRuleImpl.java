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

package io.appactive.rule.traffic.impl.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.appactive.channel.file.FileReadDataSource;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.rule.traffic.IdSourceRuleService;
import io.appactive.java.api.rule.traffic.bo.IdSourceEnum;
import io.appactive.java.api.rule.traffic.bo.IdSourceRule;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rule.base.file.FileConstant;
import io.appactive.rule.utils.FilePathUtil;
import io.appactive.support.log.LogUtil;

import java.util.LinkedList;
import java.util.List;

public class FileIdSourceRuleImpl implements IdSourceRuleService {

    private IdSourceRule idSourceRule;

    public FileIdSourceRuleImpl() {
        initFromFile(FilePathUtil.getIdSourceRulePath());
    }

    public FileIdSourceRuleImpl(String filePath) {
        initFromFile(filePath);
    }

    @Override
    public IdSourceRule getIdSourceRule() {
        return idSourceRule;
    }

    private void initFromFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw ExceptionFactory.makeFault("filePath is empty");
        }
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
        FileReadDataSource<IdSourceRule> fileReadDataSource = new FileReadDataSource<>(filePath,
            FileConstant.DEFAULT_CHARSET, FileConstant.DEFAULT_BUF_SIZE, converterInterface);
        try {
            idSourceRule = fileReadDataSource.read();
        } catch (Exception e) {
            String msg = "read file failed,e" + e.getMessage();
            LogUtil.error(msg,e);
            throw ExceptionFactory.makeFault(msg);
        }
    }


}
