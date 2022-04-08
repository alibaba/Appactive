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

package io.appactive.rule;

import io.appactive.BaseTest;
import io.appactive.java.api.rule.traffic.ForbiddenRuleService;
import io.appactive.java.api.rule.traffic.IdSourceRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.rule.traffic.TransformerRuleService;
import io.appactive.java.api.rule.traffic.bo.IdSourceEnum;
import io.appactive.java.api.rule.traffic.bo.IdSourceRule;
import io.appactive.rule.traffic.impl.ForbiddenRuleServiceImpl;
import io.appactive.rule.traffic.impl.IdSourceRuleImpl;
import io.appactive.rule.traffic.impl.TrafficRouteRuleServiceImpl;
import io.appactive.rule.traffic.impl.TransformerRuleServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class TrafficRuleTest extends BaseTest {
    String transformer_path = TEST_RESOURCE_PATH+"traffic/transformerRule.json";

    @Test
    public void testTransFormerRule(){
        TransformerRuleService modService = new TransformerRuleServiceImpl(TEST_RESOURCE_PATH+"traffic/transformerModRule.json");

        Assert.assertEquals("1",modService.getRouteIdAfterTransformer("1"));
        Assert.assertEquals("12",modService.getRouteIdAfterTransformer("12"));
        Assert.assertEquals("111",modService.getRouteIdAfterTransformer("111"));
        Assert.assertEquals("500",modService.getRouteIdAfterTransformer("1123123500"));

        TransformerRuleService wholeService = new TransformerRuleServiceImpl(TEST_RESOURCE_PATH+"traffic/transformerWholeRule.json");

        Assert.assertEquals("1",wholeService.getRouteIdAfterTransformer("1"));
        Assert.assertEquals("12",wholeService.getRouteIdAfterTransformer("12"));
        Assert.assertEquals("111",wholeService.getRouteIdAfterTransformer("111"));
        Assert.assertEquals("1123123500",wholeService.getRouteIdAfterTransformer("1123123500"));
    }

    @Test
    public void testForbiddenRule(){
        String path = TEST_RESOURCE_PATH+"traffic/forbiddenRule.json";
        TransformerRuleService wholeService = new TransformerRuleServiceImpl(TEST_RESOURCE_PATH+"traffic/transformerWholeRule.json");

        ForbiddenRuleService forbiddenRuleService = new ForbiddenRuleServiceImpl(path,wholeService);
        Assert.assertFalse(forbiddenRuleService.isRouteIdForbidden("A"));
        Assert.assertFalse(forbiddenRuleService.isRouteIdForbidden("B"));
        Assert.assertTrue(forbiddenRuleService.isRouteIdForbidden("100"));
        Assert.assertTrue(forbiddenRuleService.isRouteIdForbidden("499"));
        Assert.assertFalse(forbiddenRuleService.isRouteIdForbidden("D"));
        Assert.assertFalse(forbiddenRuleService.isRouteIdForbidden("1499"));
    }

    @Test
    public void testTrafficRule(){
        String path = TEST_RESOURCE_PATH+"traffic/unitMappingRule.json";
        TransformerRuleService wholeService = new TransformerRuleServiceImpl(TEST_RESOURCE_PATH+"traffic/transformerWholeRule.json");

        TrafficRouteRuleService trafficRouteRuleService = new TrafficRouteRuleServiceImpl(path,wholeService);
        Assert.assertNull(trafficRouteRuleService.getUnitByRouteId("A"));
        Assert.assertNull(trafficRouteRuleService.getUnitByRouteId("B"));
        Assert.assertNull(trafficRouteRuleService.getUnitByRouteId("C"));
        Assert.assertEquals("BJ",trafficRouteRuleService.getUnitByRouteId("100"));
        Assert.assertEquals("BJ",trafficRouteRuleService.getUnitByRouteId("500"));
        // accurate
        Assert.assertEquals("BJ",trafficRouteRuleService.getUnitByRouteId("499"));

        Assert.assertEquals("NH",trafficRouteRuleService.getUnitByRouteId("800"));
        Assert.assertEquals("NH",trafficRouteRuleService.getUnitByRouteId("900"));
        Assert.assertNull(trafficRouteRuleService.getUnitByRouteId("H"));
    }

    @Test
    public void idSourceRule(){
        IdSourceRuleService idSourceRuleService = new IdSourceRuleImpl(TEST_RESOURCE_PATH+"traffic/idSource.json");
        IdSourceRule idSourceRule = idSourceRuleService.getIdSourceRule();

        Assert.assertNotNull(idSourceRule);
        Assert.assertEquals("r_id",idSourceRule.getTokenKey());
        Assert.assertEquals(
                new LinkedList<IdSourceEnum>(){{
                    add(IdSourceEnum.arg);
                    add(IdSourceEnum.header);
                    add(IdSourceEnum.cookie);
                }}.toString(),
                idSourceRule.getSourceList().toString());
    }
}
