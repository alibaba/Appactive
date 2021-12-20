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
