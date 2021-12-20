package io.appactive.rule.traffic.impl.file;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;

import io.appactive.channel.file.FileReadDataSource;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.rule.traffic.TransformerRuleService;
import io.appactive.rule.traffic.bo.UnitMappingRuleBO;
import io.appactive.rule.traffic.condition.ConditionUtil;
import io.appactive.rule.traffic.condition.RuleCondition;
import io.appactive.rule.traffic.impl.file.base.BaseFileRuleService;
import io.appactive.rule.utils.FilePathUtil;
import io.appactive.support.log.LogUtil;

public class FileTrafficRouteRuleServiceImpl extends BaseFileRuleService implements TrafficRouteRuleService {

    private TransformerRuleService transformerRuleService;

    private List<TrafficCondition> trafficConditionList = new ArrayList<>();


    @Override
    public String getUnitByRouteId(String routeId) {
        String innerId = transformerRuleService.getRouteIdAfterTransformer(routeId);

        for (TrafficCondition trafficCondition : trafficConditionList) {
            boolean accept = trafficCondition.getCondition().accept(innerId);
            if (accept){
                return trafficCondition.getUnitFlag();
            }
        }
        return null;
    }

    @Override
    public boolean haveTrafficRule() {
        return !trafficConditionList.isEmpty();
    }

    @Override
    public void setTransformerRuleService(TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
    }

    public FileTrafficRouteRuleServiceImpl() {
        initFromFile(FilePathUtil.getTrafficRouteRulePath());
    }

    public FileTrafficRouteRuleServiceImpl(TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
        initFromFile(FilePathUtil.getTrafficRouteRulePath());
    }

    public FileTrafficRouteRuleServiceImpl(String filePath, TransformerRuleService transformerRuleService) {
        this.transformerRuleService = transformerRuleService;
        initFromFile(filePath);
    }



    private final DataListener<UnitMappingRuleBO> listener = new DataListener<UnitMappingRuleBO>() {
        @Override
        public String getListenerName() {
            return "TrafficRouteRule-Listener-"+this.hashCode();
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
            List<TrafficCondition> trafficConditions = new ArrayList<>();
            Map<String, List<RuleCondition>> unitConditionMap = ConditionUtil.getUnitConditionMap(unitMappingRule);
            for (Entry<String, List<RuleCondition>> entry : unitConditionMap.entrySet()) {
                String unitFlag = entry.getKey().toUpperCase();
                List<RuleCondition> ruleConditions = entry.getValue();
                for (RuleCondition ruleCondition : ruleConditions) {
                    int priority = ruleCondition.priority();
                    TrafficCondition trafficCondition = new TrafficCondition(unitFlag,ruleCondition,priority);
                    trafficConditions.add(trafficCondition);
                }
            }

            Comparator<TrafficCondition> comparator = Comparator.comparingInt(TrafficCondition::getPriority);
            trafficConditions.sort(comparator);
            trafficConditionList = trafficConditions;
        }
    };

    private void initFromFile(String filePath) {
        FileReadDataSource<UnitMappingRuleBO> dataSource
            = getUnitMappingRuleReadDataSource(filePath);
        dataSource.addDataChangedListener(listener);
    }



    class TrafficCondition {
        private String unitFlag;
        private RuleCondition condition;
        private int priority;

        public TrafficCondition(String unitFlag, RuleCondition condition, int priority) {
            this.unitFlag = unitFlag;
            this.condition = condition;
            this.priority = priority;
        }

        public String getUnitFlag() {
            return unitFlag;
        }

        public RuleCondition getCondition() {
            return condition;
        }

        public int getPriority() {
            return priority;
        }
    }
}
