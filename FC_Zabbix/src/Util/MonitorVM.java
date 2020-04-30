package Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

public class MonitorVM implements Action {

	@Override
	public void doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
		String siteUri = siteBasicInfoList.get(0).getUri();
		QueryVmsReq queryVmsReq = new QueryVmsReq();
		queryVmsReq.setLimit(1);
		queryVmsReq.setOffset(0);
		queryVmsReq.setName(instanceName);
		// 获取VmResource接口的实现
		VmResource instance = ServiceFactory.getService(VmResource.class, clientProvider);
		FCSDKResponse<PageList<VmInfo>> response = instance.queryVMs(queryVmsReq, siteUri);
		if (!response.getErrorCode().equals("00000000")) {
			System.out.println("QUERY VM info FAILED");
		} else {
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
				System.out.println(Float.parseFloat("0"));
			}else {
				System.out.println(Float.parseFloat(value));
			}
		}
	}

}
