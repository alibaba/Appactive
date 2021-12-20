package io.appactive.rpc.apache;

import io.appactive.support.sys.SysUtil;
import org.junit.Test;

public class ProtocolTest {

    @Test
    public void printIp(){
        System.out.printf("IP:"+ SysUtil.getCurrentIP());

    }
}
