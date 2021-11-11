package com.dcits.zabbixagentapiservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSON;
import com.huawei.esdk.fusioncompute.local.ServiceFactory;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.FCSDKResponse;
import com.huawei.esdk.fusioncompute.local.model.PageList;
import com.huawei.esdk.fusioncompute.local.model.common.QueryObjectmetricReq;
import com.huawei.esdk.fusioncompute.local.model.common.QueryObjectmetricResp;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.vm.QueryVmsReq;
import com.huawei.esdk.fusioncompute.local.model.vm.VmInfo;
import com.huawei.esdk.fusioncompute.local.resources.common.MonitorResource;
import com.huawei.esdk.fusioncompute.local.resources.vm.VmResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorVM implements Action {
    /**
     * 错误码
     */
    public static String ERROR_CODE = "00000000";
    private static Logger logger = LoggerFactory.getLogger(MonitorVM.class);

    @Override
    public String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
        String result = null;
        String siteUri = siteBasicInfoList.get(0).getUri();
        QueryVmsReq queryVmsReq = new QueryVmsReq();
        queryVmsReq.setLimit(1);
        queryVmsReq.setOffset(0);
        queryVmsReq.setUuid(instanceName);
        // 获取VmResource接口的实现
        VmResource instance = ServiceFactory.getService(VmResource.class, clientProvider);
        FCSDKResponse<PageList<VmInfo>> response = instance.queryVMs(queryVmsReq, siteUri);
        if (!response.getErrorCode().equals(ERROR_CODE)) {
            logger.error("QUERY VM info FAILED");
        } else {
            if (response.getResult().getList().size()==0){
                return String.valueOf(0);
            }
            logger.debug("VM INFO: "+ JSON.toJSONString(response));
            List<String> metricId = new ArrayList<String>();
            metricId.add(metric);
//			metricId.add("cpu_usage");
//			metricId.add("mem_usage");
//			metricId.add("disk_io_in");
//			metricId.add("disk_io_out");
//			metricId.add("nic_byte_in");
//			metricId.add("nic_byte_out");
//			metricId.add("disk_usage");
            QueryObjectmetricReq queryObjectmetricReq = new QueryObjectmetricReq();
            queryObjectmetricReq.setUrn(response.getResult().getList().get(0).getUrn());
            queryObjectmetricReq.setMetricId(metricId);
            List<QueryObjectmetricReq> reqs = new ArrayList<QueryObjectmetricReq>();
            reqs.add(queryObjectmetricReq);
            MonitorResource monitorResource = ServiceFactory.getService(MonitorResource.class, clientProvider);
            FCSDKResponse<QueryObjectmetricResp> queryObjectmetricResp = monitorResource.queryObjectmetricRealtimedata(siteUri, reqs);
            String value = queryObjectmetricResp.getResult().getItems().get(0).getValue().get(0).getMetricValue();
            if (value.isEmpty()) {
                result = String.valueOf(Float.parseFloat("0"));
                logger.debug(String.valueOf(Float.parseFloat("0")));
            } else {
                result = String.valueOf(Float.parseFloat(value));
                logger.debug(String.valueOf(Float.parseFloat(value)));
            }
        }
        return result;
    }
}
