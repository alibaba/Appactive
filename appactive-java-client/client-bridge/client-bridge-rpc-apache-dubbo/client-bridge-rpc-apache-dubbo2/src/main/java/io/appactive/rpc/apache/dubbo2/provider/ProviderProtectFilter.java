package io.appactive.rpc.apache.dubbo2.provider;

import io.appactive.java.api.base.AppContextClient;
import io.appactive.java.api.base.constants.ResourceActiveType;
import io.appactive.java.api.bridge.rpc.constants.constant.RPCConstant;
import io.appactive.java.api.bridge.rpc.provider.RPCProviderProtectService;
import io.appactive.java.api.rule.TrafficMachineService;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.traffic.TrafficRouteRuleService;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rpc.apache.dubbo2.utils.ModelUtil;
import io.appactive.rule.ClientRuleService;
import io.appactive.support.log.LogUtil;
import io.appactive.support.sys.SysUtil;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.model.ProviderModel;
import org.apache.dubbo.rpc.model.ServiceMetadata;

import java.text.MessageFormat;
import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * consumer's data to provider
 */
@Activate(group = PROVIDER, order = 500)
public class ProviderProtectFilter implements Filter, RPCProviderProtectService<Invocation, ProviderModel, ServiceMetadata> {

    private static final String CURRENT_IP = SysUtil.getCurrentIP();

    private final TrafficRouteRuleService trafficRouteRuleService = ClientRuleService.getTrafficRouteRuleService();
    private final AbstractMachineUnitRuleService machineUnitRuleService = ClientRuleService.getMachineUnitRuleService();
    private final TrafficMachineService trafficMachineService = new TrafficMachineService(trafficRouteRuleService,
        machineUnitRuleService);

    public ProviderProtectFilter() {
        LogUtil.info("init-ProviderProtectFilter");
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            boolean isAppactive = trafficRouteRuleService.haveTrafficRule();
            if (!isAppactive) {
                // normal
                return invoker.invoke(invocation);
            }
            // have traffic rule
            Result result = processUnitCondition(invocation);
            if (result != null) {
                return result;
            }

            return invoker.invoke(invocation);
        } finally {
            AppContextClient.clearUnitContext();
        }
    }

    @Override
    public Result normalResourceProtect(Invocation invocation, ServiceMetadata serviceMetadata) {
        return null;
    }

    @Override
    public Result unitResourceProtect(Invocation invocation, ProviderModel providerModel) {
        return processUnitConditionForUnitService(invocation, providerModel);
    }

    @Override
    public Result centerResourceProtect(Invocation invocation, ServiceMetadata serviceMetadata) {
        if (machineUnitRuleService.isCenterUnit()) {
            return null;
        }
        String serviceUniqueName = serviceMetadata.getServiceKey();
        String methodName = invocation.getMethodName();
        String clientIp = RpcContext.getContext().getRemoteHost();
        // 非中心单元接受到请求，单元保护报错
        return doNotInCenterUnit(clientIp, serviceUniqueName, methodName, invocation);
    }

    private Result processUnitCondition(Invocation invocation) {
        ProviderModel providerModel = ModelUtil.getProviderModel(invocation);
        ServiceMetadata serviceMetadata = providerModel.getServiceMetadata();

        // 单元化保护
        try {
            Map<String, Object> attachments = serviceMetadata.getAttachments();
            String resourceActiveType = (String)attachments.get(RPCConstant.URL_RESOURCE_ACTIVE_LABEL_KEY);
            if (resourceActiveType == null) {
                // 1. 普通服务不处理
                return normalResourceProtect(invocation,serviceMetadata);
            }

            if (resourceActiveType.equals(ResourceActiveType.CENTER_RESOURCE_TYPE)) {
                return centerResourceProtect(invocation, serviceMetadata);
            }

            if (resourceActiveType.equals(ResourceActiveType.UNIT_RESOURCE_TYPE)) {
                return unitResourceProtect(invocation,providerModel);
            }
            return normalResourceProtect(invocation,serviceMetadata);

        } catch (Throwable t) {
            String serviceUniqueName = serviceMetadata.getServiceKey();
            String methodName = invocation.getMethodName();
            String clientIp = RpcContext.getContext().getRemoteHost();

            String errorMsg = MessageFormat.format(
                "[Dubbo-Provider-{0}] ERROR, The request for [{1}] [{2}] from [{3}] ,currentUnit [{4}],error:{5}",
                CURRENT_IP, serviceUniqueName, methodName, clientIp,
                machineUnitRuleService.getCurrentUnit(),
                t.getMessage());
            return errorResult(invocation, errorMsg, t);
        }
    }

    private Result processUnitConditionForUnitService(Invocation invocation, ProviderModel providerModel) {
        ServiceMetadata serviceMetadata = providerModel.getServiceMetadata();
        String serviceUniqueName = serviceMetadata.getServiceKey();
        String methodName = invocation.getMethodName();
        String clientIp = RpcContext.getContext().getRemoteHost();

        String routeId = getRouteId(providerModel, invocation);
        if (StringUtils.isBlank(routeId)) {
            String errorMsg = MessageFormat.format(
                "[Dubbo-Provider-{0}] The request for [{1}] [{2}] from [{3}] is " +
                    "rejected by UnitRule Protection, because unit id is empty",
                CURRENT_IP, serviceUniqueName, methodName, clientIp);
            LogUtil.error(errorMsg);
        }
        if (trafficMachineService.isInCurrentUnit(routeId)) {

            AppContextClient.setUnitContext(routeId);
            return null;
        }
        return doNotInCurrentUnit(clientIp, serviceUniqueName, methodName, routeId,
            invocation);

    }

    private String getRouteId(ProviderModel providerModel, Invocation invocation) {
        String routerId;

        String routeIdFromParams = getFromParams(providerModel, invocation);
        if (routeIdFromParams != null) { return routeIdFromParams; }

        // 2.  consumer 透传过来的值
        Object uid = invocation.getObjectAttachment((RPCConstant.CONSUMER_REMOTE_ROUTE_ID_KEY));
        if (uid != null) {
            routerId = String.valueOf(uid);
            return routerId;
        }


        return null;
    }

    private String getFromParams(ProviderModel providerModel, Invocation invocation) {
        String routerId;
        Object[] args = invocation.getArguments();
        String routeKey = (String)providerModel.getServiceMetadata().getAttachments().get(
            RPCConstant.URL_ROUTE_INDEX_KEY);
        int route = -1;
        if (routeKey != null && !routeKey.isEmpty()) {
            route = Integer.parseInt(routeKey);
        }
        if (route >= 0 && args != null && args.length > 0) {
            routerId = String.valueOf(args[route]);
            return routerId;
        }
        return null;
    }

    private Result doNotInCurrentUnit(String clientIp, String serviceUniqueName, String methodName, String routeId,
                                      Invocation invocation) {
        String errorMsg = MessageFormat.format("[Dubbo-Provider-{0}] The request for [{1}] [{2}] from [{3}] is " +
                "rejected by UnitRule Protection, targetUnit [{4}], currentUnit [{5}].",
            CURRENT_IP, serviceUniqueName, methodName, clientIp, trafficRouteRuleService.getUnitByRouteId(routeId),
            machineUnitRuleService.getCurrentUnit());
        return errorResult(invocation, errorMsg);
    }

    private Result doNotInCenterUnit(String clientIp, String serviceUniqueName, String methodName,
                                     Invocation invocation) {
        String errorMsg = MessageFormat.format("[Dubbo-Provider-{0}] The request for [{1}] [{2}] from [{3}] is " +
                "rejected by UnitRule-CENTER Protection, targetUnit [CENTER], currentUnit [{4}].",
            CURRENT_IP, serviceUniqueName, methodName, clientIp, machineUnitRuleService.getCurrentUnit());

        return errorResult(invocation, errorMsg);

    }

    private AsyncRpcResult errorResult(Invocation invocation, String errorMsg) {
        return errorResult(invocation, errorMsg, null);
    }

    private AsyncRpcResult errorResult(Invocation invocation, String errorMsg, Throwable t) {
        if (t == null) {
            LogUtil.error(errorMsg);
        } else {
            LogUtil.error(errorMsg, t);
        }
        AppResponse a = new AppResponse(errorMsg);
        return AsyncRpcResult.newDefaultAsyncResult(a, invocation);
    }

}
