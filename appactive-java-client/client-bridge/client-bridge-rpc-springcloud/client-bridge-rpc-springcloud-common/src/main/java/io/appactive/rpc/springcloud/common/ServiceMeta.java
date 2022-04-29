package io.appactive.rpc.springcloud.common;

/**
 *
 */
public class ServiceMeta implements Comparable<ServiceMeta>{

    private String uriPrefix;
    private String ra;

    public ServiceMeta() {
    }

    public ServiceMeta(String uriPrefix, String ra) {
        this.uriPrefix = uriPrefix;
        this.ra = ra;
    }



    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    @Override
    public String toString() {
        return "ServiceMeta{" +
                "uriPrefix='" + uriPrefix + '\'' +
                ", ra=" + ra +
                '}';
    }

    @Override
    public int compareTo(ServiceMeta o) {
        int pre = this.uriPrefix.compareTo(o.getUriPrefix());
        return pre == 0 ? this.ra.compareTo(o.getRa()) : pre;
    }

}
