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

package io.appactive.java.api.bridge.db.constants;

import io.appactive.java.api.base.constants.AppactiveConstant;
import io.appactive.java.api.base.enums.PriorityLevel;

/**
 * db data  scope object
 */
public class DataScope {

    public static final DataScope SCOPE_ALL = new DataScope("*", "*");

    private String instanceId;

    private String databaseName;

    private String port;

    public DataScope(String instanceId, String databaseName) {
        this.instanceId = instanceId;
        this.databaseName = databaseName;
    }

    public DataScope(String instanceId, String databaseName, String port) {
        this.instanceId = instanceId;
        this.databaseName = databaseName;
        this.port = port;
    }

    public int priority() {
        if (this.isMatchAll()) {
            return PriorityLevel.LevelTwo.getLevel();
        }
        return PriorityLevel.LevelOne.getLevel();
    }

    public String scopeKey() {
        if (port == null) {
            return String.format("%s-%s", this.instanceId, this.databaseName);
        }

        return String.format("%s-%s-%s", this.instanceId, this.databaseName, this.port);
    }

    public boolean isMatchAll() {
        boolean instanceAndDbAllMatchAll = AppactiveConstant.ALL_MATH_TAG.equals(this.instanceId) && AppactiveConstant.ALL_MATH_TAG.equals(
            this.databaseName);
        if (port == null) {
            return instanceAndDbAllMatchAll;
        }

        return instanceAndDbAllMatchAll && AppactiveConstant.ALL_MATH_TAG.equals(this.port);
    }

    /**
     * @return the instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @param instanceId the instanceId to set
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @param databaseName the databaseName to set
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        DataScope dataScope = (DataScope)o;

        if (getInstanceId() != null ? !getInstanceId().equals(dataScope.getInstanceId())
            : dataScope.getInstanceId() != null) { return false; }
        if (getDatabaseName() != null ? !getDatabaseName().equals(dataScope.getDatabaseName())
            : dataScope.getDatabaseName() != null) { return false; }
        return getPort() != null ? getPort().equals(dataScope.getPort()) : dataScope.getPort() == null;
    }

    @Override
    public int hashCode() {
        int result = getInstanceId() != null ? getInstanceId().hashCode() : 0;
        result = 31 * result + (getDatabaseName() != null ? getDatabaseName().hashCode() : 0);
        result = 31 * result + (getPort() != null ? getPort().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataScope{" +
            "instanceId='" + instanceId + '\'' +
            ", databaseName='" + databaseName + '\'' +
            ", port='" + port + '\'' +
            '}';
    }
}
