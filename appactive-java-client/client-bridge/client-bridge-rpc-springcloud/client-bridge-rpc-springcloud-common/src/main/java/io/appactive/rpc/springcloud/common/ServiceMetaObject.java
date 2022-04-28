package io.appactive.rpc.springcloud.common;

import java.util.List;

/**
 *
 */
public class ServiceMetaObject {

    private List<ServiceMeta> serviceMetaList;
    /**
     * string of serviceMetaList
     */
    private String meta;
    private String md5;

    public List<ServiceMeta> getServiceMetaList() {
        return serviceMetaList;
    }

    public void setServiceMetaList(List<ServiceMeta> serviceMetaList) {
        this.serviceMetaList = serviceMetaList;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public ServiceMetaObject(List<ServiceMeta> serviceMetaList, String md5) {
        this.serviceMetaList = serviceMetaList;
        this.md5 = md5;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public ServiceMetaObject() {
    }

    @Override
    public String toString() {
        return "ServiceMetaObject{" +
                "serviceMetaList=" + serviceMetaList +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
