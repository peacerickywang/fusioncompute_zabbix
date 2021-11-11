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
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.vm.QueryVmsReq;
import com.huawei.esdk.fusioncompute.local.model.vm.VmInfo;
import com.huawei.esdk.fusioncompute.local.resources.vm.VmResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoverVMs implements Action{
    /**
     * 错误码
     */
    public static String ERROR_CODE = "00000000";
    private static Logger logger = LoggerFactory.getLogger(DiscoverVMs.class);

    @Override
    public String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
        JsonObject result = new JsonObject();
        QueryVmsReq req = new QueryVmsReq();
        req.setDetail(0);
        req.setLimit(100);
        int current = 0;
        int offset = 0;
        boolean errorFlag = false;
        // 获取VmResource接口的实现
        VmResource instance = ServiceFactory.getService(VmResource.class, clientProvider);
        List<VmInfo> vmInfosAll = new ArrayList<>();
        for (SiteBasicInfo siteBasicInfo : siteBasicInfoList) {
            while (true) {
                req.setOffset(offset);
                FCSDKResponse<PageList<VmInfo>> response = instance.queryVMs(null, siteBasicInfo.getUri());
                if (!response.getErrorCode().equals(ERROR_CODE)) {
                    errorFlag = true;
                    logger.error("QUERY VMs FAILED. Query info: "+JSON.toJSONString(clientProvider));
                }
                if (response.getResult().getList().size() > 0) {
                    vmInfosAll.addAll(response.getResult().getList());
                    current = current + response.getResult().getList().size();
                    if (response.getResult().getTotal() <= current) {
                        break;
                    } else {
                        offset = offset + 100;
                    }
                } else {
                    break;
                }
            }
            if (errorFlag) {
                logger.error("QUERY VMs FAILED. Query info: "+JSON.toJSONString(clientProvider));
            } else {
                JsonArray jsonArray = new JsonArray();
                for (VmInfo vmInfo : vmInfosAll) {
                    //忽略镜像
                    if (!vmInfo.getIsTemplate()) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("{#VMNAME}", vmInfo.getUuid());
                        //因uri和urn都含有特殊字符zabbix不支持，进行加密
//						jsonObject.addProperty("{#VMURN}", Base64.getEncoder().encodeToString(vmInfo.getUrn().getBytes()));
//						jsonObject.addProperty("{#VMURN}", vmInfo.getUrn().replace(":", "?"));
                        jsonArray.add(jsonObject);
                    }
                }
                result.add("data", jsonArray);
                logger.debug(result.toString());
            }
        }
        return result.toString();
    }
}
