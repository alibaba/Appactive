package io.appactive.db.mysql.rule;

import io.appactive.java.api.bridge.db.constants.DataScope;
import io.appactive.java.api.bridge.db.scope.DataScopeService;

public class DataScopeServiceImpl implements DataScopeService {


    @Override
    public boolean isInAppactiveList(DataScope scope) {
        return false;
    }
}
