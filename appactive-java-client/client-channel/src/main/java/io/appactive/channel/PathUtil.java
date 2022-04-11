package io.appactive.channel;

import java.util.Properties;

public interface PathUtil {

    String getMachineRulePath() ;

    String getDataScopeRuleDirectoryPath() ;

    String getForbiddenRulePath() ;

    String getTrafficRouteRulePath() ;

    String getTransformerRulePath() ;

    String getIdSourceRulePath() ;

    String getConfigServerAddress();

    Properties getAuth();

    Properties getExtras();
}
