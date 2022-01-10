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

package io.appactive.db.mysql.utils;

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 *
 */
public class SQLCacheCheckUtil {

    public static void checkDruidSqlCache() {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            Set<ObjectInstance> instances = mbeanServer
                .queryMBeans(new ObjectName("com.alibaba.druid:type=DruidDataSource,*"), null);
            Iterator<ObjectInstance> iterator = instances.iterator();
            while (iterator.hasNext()) {
                ObjectInstance dataSource = iterator.next();
                MBeanInfo mBeanInfo = mbeanServer.getMBeanInfo(dataSource.getObjectName());
                MBeanAttributeInfo[] mBeanAttributeInfos = mBeanInfo.getAttributes();
                for (MBeanAttributeInfo mBeanAttributeInfo : mBeanAttributeInfos) {
                    String attributeInfoName = mBeanAttributeInfo.getName();
                    checkPoolPreparedStatements(mbeanServer, dataSource, mBeanAttributeInfo, attributeInfoName);
                }
            }
        } catch (ReflectionException e) {
            // ignore such exception
        } catch (InstanceNotFoundException e) {
            // ignore such exception
        } catch (IntrospectionException e) {
            // ignore such exception
        } catch (AttributeNotFoundException e) {
            // ignore such exception
        } catch (MBeanException e) {
            // ignore such exception
        } catch (MalformedObjectNameException e) {
            // ignore such exception
        }
    }

    private static void checkPoolPreparedStatements(MBeanServer mbeanServer, ObjectInstance dataSource,
                                  MBeanAttributeInfo mBeanAttributeInfo, String attributeInfoName)
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        if ("PoolPreparedStatements".equals(attributeInfoName)) {
            Object val = mbeanServer.getAttribute(dataSource.getObjectName(), attributeInfoName);
            if (val != null && "boolean".equals(mBeanAttributeInfo.getType())) {
                Boolean v = (Boolean)val;
                if (v) {
                    String sb = "Druid datasource poolPreparedStatements:" + true
                        + " or MaxPoolPreparedStatementPerConnectionSize > 0, please set it manually!";
                    throw new RuntimeException(sb);
                }
            }
        }
    }
}
