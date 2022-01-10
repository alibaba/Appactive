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

package io.appactive.rule.traffic.condition.impl.between;

import io.appactive.support.lang.AssertUtil;

public class NumTokenRange {

    public boolean isInRange(Long tokenValue) {
        if (startToken <= tokenValue && tokenValue <= endToken) {
            return true;
        }
        return false;
    }

    private Long startToken;

    private Long endToken;


    public NumTokenRange(Long startToken, Long endToken){
        this.startToken = startToken;
        this.endToken = endToken;

        // 验证数字范围
        AssertUtil.assertGreatZero("NumTokenRange.startToken", this.startToken);
        AssertUtil.assertGreatZero("NumTokenRange.endToken", this.endToken);
        AssertUtil.assertGreatEquals("NumTokenRange.endToken", "NumTokenRange.startToken", this.endToken, this.startToken);
    }
}
