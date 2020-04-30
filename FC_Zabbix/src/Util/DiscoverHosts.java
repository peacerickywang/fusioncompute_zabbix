package Util;

import java.util.List;
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

public class DiscoverHosts implements Action{
	
	/**
     * 错误码
     */
    public static String ERROR_CODE = "00000000";

	@Override
	public void doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
		// 获取HostResource接口的实现
					HostResource service = ServiceFactory.getService(HostResource.class, clientProvider);
					QueryHostListReq req = new QueryHostListReq();
					req.setLimit(100);
					req.setOffset(0);
					req.setResourceGroupFlag(1);
					req.setName("");
					for (SiteBasicInfo siteBasicInfo : siteBasicInfoList) {
						FCSDKResponse<PageList<HostBasicInfo>> resp = service.queryHostList(siteBasicInfo.getUri(), req);
						if (!resp.getErrorCode().equals(ERROR_CODE)) {
							System.out.println("QUERY HOSTs FAILED");
						}else {
							JsonObject result = new JsonObject();
							JsonArray jsonArray = new JsonArray();
							for (HostBasicInfo hostBasicInfo : resp.getResult().getList()) {
								JsonObject jsonObject = new JsonObject();
								jsonObject.addProperty("{#HOSTNAME}", hostBasicInfo.getName());
								//因uri和urn都含有特殊字符zabbix不支持，进行替换
//								jsonObject.addProperty("{#HOSTURN}", Base64.getEncoder().encodeToString(hostBasicInfo.getUrn().getBytes()));
//								jsonObject.addProperty("{#HOSTURN}", hostBasicInfo.getUrn().replace(":", "?"));
								jsonArray.add(jsonObject);
							}
							result.add("data", jsonArray);
							System.out.println(result);
						}
					
					}
		
	}

}
