package com.dcits.zabbixagentapiservice.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huawei.esdk.fusioncompute.local.ServiceFactory;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.FCSDKResponse;
import com.huawei.esdk.fusioncompute.local.model.PageList;
import com.huawei.esdk.fusioncompute.local.model.host.HostBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.host.QueryHostListReq;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.resources.host.HostResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoverHosts implements Action {
    /**
     * 错误码
     */
    public static String ERROR_CODE = "00000000";
    private static Logger logger = LoggerFactory.getLogger(DiscoverHosts.class);

    @Override
    public String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
        JsonObject result = new JsonObject();
        // 获取HostResource接口的实现
        HostResource service = ServiceFactory.getService(HostResource.class, clientProvider);
        QueryHostListReq req = new QueryHostListReq();
        req.setLimit(100);
        req.setResourceGroupFlag(1);
        req.setName("");
        List<HostBasicInfo> hostBasicInfoList = new ArrayList<>();
        int offset = 0;
        int currentTotal = 0;
        boolean errorFlag = false;
        for (SiteBasicInfo siteBasicInfo : siteBasicInfoList) {
            while (true) {
                req.setOffset(offset);
                FCSDKResponse<PageList<HostBasicInfo>> resp = service.queryHostList(siteBasicInfo.getUri(), req);
                if (!resp.getErrorCode().equals(ERROR_CODE)) {
                    errorFlag = true;
                    logger.error("FusionCompute Host discover failed. Query info: "+JSON.toJSONString(clientProvider));
                }
                if (resp.getResult().getList().size() > 0) {
                    hostBasicInfoList.addAll(resp.getResult().getList());
                    currentTotal = currentTotal + resp.getResult().getList().size();
                    if (resp.getResult().getTotal() <= currentTotal) {
                        break;
                    } else {
                        offset = offset + 100;
                    }
                }
            }
            if (errorFlag) {
                logger.error("FusionCompute Host discover failed. Query info: "+JSON.toJSONString(clientProvider));
            } else {
                JsonArray jsonArray = new JsonArray();
                for (HostBasicInfo hostBasicInfo : hostBasicInfoList) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("{#HOSTNAME}", hostBasicInfo.getName());
                    //因uri和urn都含有特殊字符zabbix不支持，进行替换
//								jsonObject.addProperty("{#HOSTURN}", Base64.getEncoder().encodeToString(hostBasicInfo.getUrn().getBytes()));
//								jsonObject.addProperty("{#HOSTURN}", hostBasicInfo.getUrn().replace(":", "?"));
                    jsonArray.add(jsonObject);
                }
                result.add("data", jsonArray);
                logger.debug(jsonArray.toString());
            }
        }
        return result.toString();
    }

}
