package io.appactive.rule;

import io.appactive.BaseTest;
import io.appactive.java.api.rule.traffic.ForbiddenRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.rule.traffic.TransformerRuleService;
import io.appactive.rule.traffic.impl.file.FileForbiddenRuleServiceImpl;
import io.appactive.rule.traffic.impl.file.FileTrafficRouteRuleServiceImpl;
import io.appactive.rule.traffic.impl.file.FileTransformerRuleServiceImpl;
import org.junit.Assert;
import org.junit.Test;

public class TrafficRuleTest extends BaseTest {
    String transformer_path = TEST_RESOURCE_PATH+"traffic/transformerRule.json";

    @Test
    public void testTransFormerRule(){
        TransformerRuleService modService = new FileTransformerRuleServiceImpl(TEST_RESOURCE_PATH+"traffic/transformerModRule.json");

        Assert.assertEquals("1",modService.getRouteIdAfterTransformer("1"));
        Assert.assertEquals("12",modService.getRouteIdAfterTransformer("12"));
        Assert.assertEquals("111",modService.getRouteIdAfterTransformer("111"));
        Assert.assertEquals("500",modService.getRouteIdAfterTransformer("1123123500"));

        TransformerRuleService wholeService = new FileTransformerRuleServiceImpl(TEST_RESOURCE_PATH+"traffic/transformerWholeRule.json");

        Assert.assertEquals("1",wholeService.getRouteIdAfterTransformer("1"));
        Assert.assertEquals("12",wholeService.getRouteIdAfterTransformer("12"));
        Assert.assertEquals("111",wholeService.getRouteIdAfterTransformer("111"));
        Assert.assertEquals("1123123500",wholeService.getRouteIdAfterTransformer("1123123500"));
    }


    @Test
    public void testForbiddenRule(){
        String path = TEST_RESOURCE_PATH+"traffic/forbiddenRule.json";
        TransformerRuleService wholeService = new FileTransformerRuleServiceImpl(TEST_RESOURCE_PATH+"traffic/transformerWholeRule.json");

        ForbiddenRuleService forbiddenRuleService = new FileForbiddenRuleServiceImpl(path,wholeService);
        Assert.assertTrue(forbiddenRuleService.isRouteIdForbidden("A"));
        Assert.assertTrue(forbiddenRuleService.isRouteIdForbidden("B"));
        Assert.assertTrue(forbiddenRuleService.isRouteIdForbidden("100"));
        Assert.assertTrue(forbiddenRuleService.isRouteIdForbidden("499"));
        Assert.assertFalse(forbiddenRuleService.isRouteIdForbidden("D"));
        Assert.assertFalse(forbiddenRuleService.isRouteIdForbidden("1499"));
    }
    @Test
    public void testTrafficRule(){
        String path = TEST_RESOURCE_PATH+"traffic/unitMappingRule.json";
        TransformerRuleService wholeService = new FileTransformerRuleServiceImpl(TEST_RESOURCE_PATH+"traffic/transformerWholeRule.json");

        TrafficRouteRuleService trafficRouteRuleService = new FileTrafficRouteRuleServiceImpl(path,wholeService);
        Assert.assertEquals("BJ",trafficRouteRuleService.getUnitByRouteId("A"));
        Assert.assertEquals("BJ",trafficRouteRuleService.getUnitByRouteId("B"));
        Assert.assertEquals("BJ",trafficRouteRuleService.getUnitByRouteId("C"));
        Assert.assertEquals("BJ",trafficRouteRuleService.getUnitByRouteId("100"));
        Assert.assertEquals("BJ",trafficRouteRuleService.getUnitByRouteId("500"));
        // accurate
        Assert.assertEquals("NH",trafficRouteRuleService.getUnitByRouteId("499"));

        Assert.assertEquals("NH",trafficRouteRuleService.getUnitByRouteId("D"));
        Assert.assertEquals("NH",trafficRouteRuleService.getUnitByRouteId("E"));
        Assert.assertEquals("NH",trafficRouteRuleService.getUnitByRouteId("F"));
        Assert.assertEquals("NH",trafficRouteRuleService.getUnitByRouteId("800"));
        Assert.assertEquals("NH",trafficRouteRuleService.getUnitByRouteId("900"));
        Assert.assertNull(trafficRouteRuleService.getUnitByRouteId("H"));
    }
}
