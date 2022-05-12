package io.appactive.rpc.springcloud.common.provider;

import com.alibaba.fastjson.JSON;
import io.appactive.java.api.base.constants.ResourceActiveType;
import io.appactive.rpc.springcloud.common.ServiceMeta;
import io.appactive.rpc.springcloud.common.ServiceMetaObject;
import io.appactive.support.lang.CollectionUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * @author mageekchiu
 */
public class URIRegister {

    private static ServiceMetaObject serviceMetaObject;

    private static final String MATCH_ALL = "/**";

    /**
     * publish to registry meta like dubbo
     * rather than building a new appactive rule type
     */
    public static void collectUris(List<FilterRegistrationBean> beanList){
        if (CollectionUtils.isNotEmpty(beanList)){
            List<ServiceMeta> serviceMetaList = new LinkedList<>();
            boolean hasWildChar = false;
            for (FilterRegistrationBean filterRegistrationBean : beanList) {
                Filter filter = filterRegistrationBean.getFilter();
                if (filter==null){
                    continue;
                }
                if (filter instanceof UnitServiceFilter){
                    Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
                    for (String urlPattern : urlPatterns) {
                        if (MATCH_ALL.equalsIgnoreCase(urlPattern)){
                            hasWildChar = true;
                        }
                        ServiceMeta serviceMeta = new ServiceMeta(urlPattern, ResourceActiveType.UNIT_RESOURCE_TYPE);
                        serviceMetaList.add(serviceMeta);
                    }
                }else if(filter instanceof CenterServiceFilter){
                    Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
                    for (String urlPattern : urlPatterns) {
                        if (MATCH_ALL.equalsIgnoreCase(urlPattern)){
                            hasWildChar = true;
                        }
                        ServiceMeta serviceMeta = new ServiceMeta(urlPattern, ResourceActiveType.CENTER_RESOURCE_TYPE);
                        serviceMetaList.add(serviceMeta);
                    }
                }else if (filter instanceof  NormalServiceFilter){
                    Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
                    for (String urlPattern : urlPatterns) {
                        if (MATCH_ALL.equalsIgnoreCase(urlPattern)){
                            hasWildChar = true;
                        }
                        ServiceMeta serviceMeta = new ServiceMeta(urlPattern, ResourceActiveType.NORMAL_RESOURCE_TYPE);
                        serviceMetaList.add(serviceMeta);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(serviceMetaList)){
                if (!hasWildChar){
                    // 保证所有 service(app+uri) 都纳入管理，不然不好做缓存管理
                    ServiceMeta serviceMeta = new ServiceMeta(MATCH_ALL, ResourceActiveType.NORMAL_RESOURCE_TYPE);
                    serviceMetaList.add(serviceMeta);
                }
                serviceMetaObject = new ServiceMetaObject();
                Collections.sort(serviceMetaList);
                serviceMetaObject.setServiceMetaList(serviceMetaList);
                String meta = JSON.toJSONString(serviceMetaList);
                serviceMetaObject.setMeta(meta);
                String md5 = DigestUtils.md5Hex(meta.getBytes(StandardCharsets.UTF_8));
                serviceMetaObject.setMd5OfList(md5);
            }
        }
    }

    public static ServiceMetaObject getServiceMetaObject() {
        return serviceMetaObject;
    }
}
