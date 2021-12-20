package io.appactive.java.api.rule.property.db;

import io.appactive.java.api.bridge.db.constants.DataScope;
import io.appactive.java.api.base.extension.SPI;

@SPI
public interface DataScopeRuleService {

    boolean isDataScopeExist(DataScope dataScope);
}
