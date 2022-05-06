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
    private String md5OfList;

    public List<ServiceMeta> getServiceMetaList() {
        return serviceMetaList;
    }

    public void setServiceMetaList(List<ServiceMeta> serviceMetaList) {
        this.serviceMetaList = serviceMetaList;
    }

    public String getMd5OfList() {
        return md5OfList;
    }

    public void setMd5OfList(String md5OfList) {
        this.md5OfList = md5OfList;
    }

    public ServiceMetaObject(List<ServiceMeta> serviceMetaList, String md5OfList) {
        this.serviceMetaList = serviceMetaList;
        this.md5OfList = md5OfList;
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
                ", md5OfList='" + md5OfList + '\'' +
                '}';
    }
}
