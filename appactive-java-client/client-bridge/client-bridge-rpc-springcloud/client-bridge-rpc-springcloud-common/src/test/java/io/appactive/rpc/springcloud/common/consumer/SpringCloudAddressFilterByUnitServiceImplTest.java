package io.appactive.rpc.springcloud.common.consumer;

import junit.framework.TestCase;
import org.junit.Assert;
import org.springframework.util.AntPathMatcher;

import java.util.HashSet;

public class SpringCloudAddressFilterByUnitServiceImplTest extends TestCase {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public void testBestMatcher() {

        Assert.assertEquals(
                SpringCloudAddressFilterByUnitServiceImpl.calcBestMatcher(antPathMatcher,new HashSet<String>(){{
                add("DEFAULT_GROUP@@product@active@/detail/*");
                add("DEFAULT_GROUP@@product@active@/**");
                add("DEFAULT_GROUP@@product@active@/detailHidden/*");
                add("DEFAULT_GROUP@@product@active@/buy/*");
            }},"DEFAULT_GROUP@@product@active@/list/"),
                "DEFAULT_GROUP@@product@active@/**");

        Assert.assertEquals(
                SpringCloudAddressFilterByUnitServiceImpl.calcBestMatcher(antPathMatcher,new HashSet<String>(){{
                    add("DEFAULT_GROUP@@product@active@/detail/*");
                    add("DEFAULT_GROUP@@product@active@/**");
                    add("DEFAULT_GROUP@@product@active@/detailHidden/*");
                    add("DEFAULT_GROUP@@product@active@/buy/*");
                }},"DEFAULT_GROUP@@product@active@/list"),
                "DEFAULT_GROUP@@product@active@/**");

        Assert.assertEquals(
                SpringCloudAddressFilterByUnitServiceImpl.calcBestMatcher(antPathMatcher,new HashSet<String>(){{
                    add("DEFAULT_GROUP@@product@active@/detail/*");
                    add("DEFAULT_GROUP@@product@active@/**");
                    add("DEFAULT_GROUP@@product@active@/*");
                    add("DEFAULT_GROUP@@product@active@/detailHidden/*");
                    add("DEFAULT_GROUP@@product@active@/buy/*");
                }},"DEFAULT_GROUP@@product@active@/list/3/4"),
                "DEFAULT_GROUP@@product@active@/**");

        Assert.assertEquals(
                SpringCloudAddressFilterByUnitServiceImpl.calcBestMatcher(antPathMatcher,new HashSet<String>(){{
                    add("DEFAULT_GROUP@@product@active@/detail/*");
                    add("DEFAULT_GROUP@@product@active@/**");
                    add("DEFAULT_GROUP@@product@active@/*");
                    add("DEFAULT_GROUP@@product@active@/detailHidden/*");
                    add("DEFAULT_GROUP@@product@active@/buy/*");
                }},"DEFAULT_GROUP@@product@active@/detail"),
                "DEFAULT_GROUP@@product@active@/*");

        Assert.assertEquals(
                SpringCloudAddressFilterByUnitServiceImpl.calcBestMatcher(antPathMatcher,new HashSet<String>(){{
                    add("DEFAULT_GROUP@@product@active@/detail/*");
                    add("DEFAULT_GROUP@@product@active@/**");
                    add("DEFAULT_GROUP@@product@active@/*");
                    add("DEFAULT_GROUP@@product@active@/detailHidden/*");
                    add("DEFAULT_GROUP@@product@active@/buy/*");
                }},"DEFAULT_GROUP@@product@active@/detail/"),
                "DEFAULT_GROUP@@product@active@/detail/*");

        Assert.assertEquals(
                SpringCloudAddressFilterByUnitServiceImpl.calcBestMatcher(antPathMatcher,new HashSet<String>(){{
                    add("DEFAULT_GROUP@@product@active@/detail/*/");
                    add("DEFAULT_GROUP@@product@active@/**");
                    add("DEFAULT_GROUP@@product@active@/*");
                    add("DEFAULT_GROUP@@product@active@/detailHidden/*");
                    add("DEFAULT_GROUP@@product@active@/buy/*");
                }},"DEFAULT_GROUP@@product@active@/detail/3/"),
                "DEFAULT_GROUP@@product@active@/detail/*/");
        Assert.assertEquals(
                SpringCloudAddressFilterByUnitServiceImpl.calcBestMatcher(antPathMatcher,new HashSet<String>(){{
                    add("DEFAULT_GROUP@@product@active@/detail/**");
                    add("DEFAULT_GROUP@@product@active@/**");
                    add("DEFAULT_GROUP@@product@active@/*");
                    add("DEFAULT_GROUP@@product@active@/detailHidden/*");
                    add("DEFAULT_GROUP@@product@active@/buy/*");
                }},"DEFAULT_GROUP@@product@active@/detail/3/"),
                "DEFAULT_GROUP@@product@active@/detail/**");
    }
}