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

package io.appactive.support.sys;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.support.log.LogUtil;
import org.slf4j.Logger;


public class SysUtil {

    private static final Logger logger = LogUtil.getLogger();

    private static String SDK_VERSION = "undefined";
    private static String CURRENT_IP = "undefined";

    public SysUtil() {
    }

    public static String getSDKVersion() {
        return SDK_VERSION;
    }

    public static String getCurrentIP() {
        return CURRENT_IP;
    }

    static {
        initSDKVersion();
        CURRENT_IP = getPrivateLocalIp();

    }

    private static String getPrivateLocalIp() {
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            if (netInterfaces == null){
                return null;
            }
            while(netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface)netInterfaces.nextElement();
                Enumeration nii = ni.getInetAddresses();

                while(nii.hasMoreElements()) {
                    ip = (InetAddress)nii.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException var4) {
            var4.printStackTrace();
        }

        return null;
    }

    private static void initSDKVersion() {
        Properties properties = new Properties();
        ClassLoader classLoader = SysUtil.class.getClassLoader();
        if (classLoader == null){
            return;
        }
        InputStream in = classLoader.getResourceAsStream("sdk-version.properties");
        if (in == null){
            return;
        }
        try {
            properties.load(in);
            String version = properties.getProperty("version");
            if (StringUtils.isNotBlank(version)) {
                SDK_VERSION = version;
            }

            logger.info("SDK_VERSION:{}", SDK_VERSION);
        } catch (Exception var11) {
            logger.error("router_00501", "[sdk property]load property error.e:" + var11.getMessage(), var11);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var10) {
                    var10.printStackTrace();
                }
            }

        }
    }
}
