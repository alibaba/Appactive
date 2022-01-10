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

package io.appactive.db.mysql.connection.proxy;

import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.Properties;

public abstract class BaseProxy implements Wrapper {

    protected Properties property;

    protected BaseProxy(Properties property){
        this.property = property;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == null) {
            return null;
        }

        if (iface == this.getClass()) {
            return (T) this;
        }

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == null) {
            return false;
        }

        if (iface == this.getClass()) {
            return true;
        }

        return false;
    }

    public <T> T unwrap(Wrapper wrapper, Class<T> iface) throws SQLException {
        return wrapper.unwrap(iface);
    }

    public boolean isWrapperFor(Wrapper wrapper, Class<?> iface) throws SQLException {
        return wrapper.isWrapperFor(iface);
    }

    public Properties getProperty() {
        return property;
    }

}
