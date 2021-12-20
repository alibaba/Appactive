package io.appactive.rule.traffic.impl.file;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.appactive.channel.file.FileReadDataSource;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.java.api.rule.traffic.ForbiddenRuleService;
import io.appactive.java.api.rule.traffic.TransformerRuleService;
import io.appactive.rule.traffic.bo.UnitMappingRuleBO;
import io.appactive.rule.traffic.condition.ConditionUtil;
import io.appactive.rule.traffic.condition.RuleCondition;
import io.appactive.rule.traffic.impl.file.base.BaseFileRuleService;
import io.appactive.rule.utils.FilePathUtil;
import io.appactive.support.log.LogUtil;

public class FileForbiddenRuleServiceImpl extends BaseFileRuleService implements ForbiddenRuleService {

    private TransformerRuleService transformerRuleService;

    @Override
    public boolean isRouteIdForbidden(String routeId) {
        if (memoryConditions == null){
            // not have rule,not forbidden
            return true;
        }
        String innerId = transformerRuleService.getRouteIdAfterTransformer(routeId);
        for (RuleCondition memoryCondition : memoryConditions) {
            if (memoryCondition.accept(innerId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setTransformerRuleService(TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
    }

    public FileForbiddenRuleServiceImpl() {
        initFromFile(FilePathUtil.getForbiddenRulePath());
    }

    public FileForbiddenRuleServiceImpl(TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
        initFromFile(FilePathUtil.getForbiddenRulePath());
    }

    public FileForbiddenRuleServiceImpl(String filePath, TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
        initFromFile(filePath);
    }

    private List<RuleCondition> memoryConditions;


    private final DataListener<UnitMappingRuleBO> listener = new DataListener<UnitMappingRuleBO>() {
        @Override
        public String getListenerName() {
            return "ForbiddenRule-Listener-"+this.hashCode();
        }

        @Override
        public void dataChanged(UnitMappingRuleBO old,UnitMappingRuleBO unitMappingRule) {
            if (!checkRuleRight(unitMappingRule)) {
                LogUtil.error("forbidden rule error,not change memory value,data:"+ JSON.toJSONString(unitMappingRule));
                return;
            }
            if (unitMappingRule == null){
                return;
            }
            Map<String, List<RuleCondition>> unitConditionMap = ConditionUtil.getUnitConditionMap(unitMappingRule);
            memoryConditions = unitConditionMap.values().iterator().next();
        }
    };

    private void initFromFile(String filePath) {
        FileReadDataSource<UnitMappingRuleBO> dataSource
            = getUnitMappingRuleReadDataSource(filePath);
        dataSource.addDataChangedListener(listener);
    }
}
