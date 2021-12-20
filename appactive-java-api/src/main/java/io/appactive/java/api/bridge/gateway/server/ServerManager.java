package io.appactive.java.api.bridge.gateway.server;

import java.util.List;

public class ServerManager {

    /**
     * 获得 某单元标下的所有server ip
     * 需要实现
     * @param unitFlag
     * @return
     */
    public List<String> getServerList( String unitFlag) {
        throw new UnsupportedOperationException();
    }

    /**
     * 设置 server ip
     * 需要实现
     * @param unitFlag
     * @param serverList
     * @return
     */
    public String setServerList(String unitFlag, List<String> serverList) {
        throw new UnsupportedOperationException();
    }

}
