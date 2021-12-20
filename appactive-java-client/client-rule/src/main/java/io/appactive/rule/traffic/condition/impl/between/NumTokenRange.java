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
