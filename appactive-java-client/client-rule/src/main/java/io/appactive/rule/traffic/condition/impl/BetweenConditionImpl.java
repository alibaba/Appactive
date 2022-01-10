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

package io.appactive.rule.traffic.condition.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.lang.AssertUtil;
import io.appactive.support.log.LogUtil;
import io.appactive.rule.traffic.condition.ConditionType;
import io.appactive.rule.traffic.condition.RuleCondition;
import io.appactive.rule.traffic.condition.impl.between.NumTokenRange;
import org.slf4j.Logger;

public class BetweenConditionImpl implements RuleCondition {

    private static final Logger logger = LogUtil.getLogger();

    public static final Pattern RANGE_PATTER = Pattern.compile("(\\d+)~(\\d+)");

    private int priority;

    private String tokenName;

    private List<NumTokenRange> rangeList;

    @Override
    public String conditionType() {
        return ConditionType.Between.getTypeName();
    }

    @Override
    public String tokenName() {
        return this.tokenName;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public void init(int conditionIndex, String routeTokenName, List<String> valueList) {
        this.priority = conditionIndex;
        this.tokenName = routeTokenName;
        AssertUtil.assertGreatZero("BetweenCondition.priority", this.priority);
        AssertUtil.assertNotBlank("BetweenCondition.tokenName", this.tokenName);
        List<NumTokenRange> rangeList = new ArrayList<>();

        for (String rangeValue : valueList) {
            Matcher m = RANGE_PATTER.matcher(rangeValue);
            if (m.matches()) {
                NumTokenRange range = new NumTokenRange(Long.valueOf(m.group(1)), Long.valueOf(m.group(2)));
                rangeList.add(range);
                continue;
            }
            throw ExceptionFactory.makeFault("between:TokenRange is invalid, should be format like 123~456 ");
        }

        this.rangeList = rangeList;

        // 至少应该有一个Range条件范围
        AssertUtil.assertGreatZero("BetweenCondition.rangeList.size()", this.rangeList.size());
    }

    @Override
    public boolean accept(String innerId) {
        if (this.rangeList == null || this.rangeList.isEmpty()) {
            throw ExceptionFactory.makeFault("between: rangeList is empty");
        }
        if (StringUtils.isBlank(innerId)) {
            throw ExceptionFactory.makeFault("between: innerId is empty");
        }

        if (StringUtils.isNotNumbers(innerId)) {
            // not number ，returen false
            return false;
        }
        Long tokenId = Long.valueOf(innerId);

        for (NumTokenRange range : rangeList) {
            if (range.isInRange(tokenId)) {
                return true;
            }
        }
        return false;
    }
}
