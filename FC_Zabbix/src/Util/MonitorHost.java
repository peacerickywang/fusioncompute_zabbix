package Util;

import java.util.ArrayList;
import java.util.List;

import com.huawei.esdk.fusioncompute.local.ServiceFactory;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.FCSDKResponse;
import com.huawei.esdk.fusioncompute.local.model.PageList;
import com.huawei.esdk.fusioncompute.local.model.common.QueryObjectmetricReq;
import com.huawei.esdk.fusioncompute.local.model.common.QueryObjectmetricResp;
import com.huawei.esdk.fusioncompute.local.model.host.HostBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.host.QueryHostListReq;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.resources.common.MonitorResource;
import com.huawei.esdk.fusioncompute.local.resources.host.HostResource;

public class MonitorHost implements Action {

	@Override
	public void doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName,
			String metric) {
		String siteUri = siteBasicInfoList.get(0).getUri();
		QueryHostListReq req = new QueryHostListReq();
		req.setLimit(1);
		req.setOffset(0);
		req.setName(instanceName);
		// 获取HostResource接口的实现
		HostResource service = ServiceFactory.getService(HostResource.class, clientProvider);
		FCSDKResponse<PageList<HostBasicInfo>> resp = service.queryHostList(siteUri, req);
		if (!resp.getErrorCode().equals("00000000")) {
			System.out.println("QUERY Host info FAILED");
		} else {
			List<String> metricId = new ArrayList<String>();
			metricId.add(metric);
			// metricId.add("cpu_usage");
			// metricId.add("mem_usage");
			// metricId.add("disk_io_in");
			// metricId.add("disk_io_out");
			// metricId.add("nic_byte_in");
			// metricId.add("nic_byte_out");
			// metricId.add("logic_disk_usage");
			// metricId.add("dom0_cpu_usage");
			// metricId.add("dom0_mem_usage");
			// metricId.add("domU_cpu_usage");
			// metricId.add("domU_mem_usage");
			QueryObjectmetricReq queryObjectmetricReq = new QueryObjectmetricReq();
			queryObjectmetricReq.setUrn(resp.getResult().getList().get(0).getUrn());
			queryObjectmetricReq.setMetricId(metricId);
			List<QueryObjectmetricReq> reqs = new ArrayList<QueryObjectmetricReq>();
			reqs.add(queryObjectmetricReq);
			MonitorResource monitorResource = ServiceFactory.getService(MonitorResource.class, clientProvider);
			FCSDKResponse<QueryObjectmetricResp> queryObjectmetricResp = monitorResource
					.queryObjectmetricRealtimedata(siteUri, reqs);
			String value = queryObjectmetricResp.getResult().getItems().get(0).getValue().get(0).getMetricValue();
			if (value.isEmpty()) {
				System.out.println(Float.parseFloat("0"));
			}else {
				System.out.println(Float.parseFloat(value));
			}
		}
	}

}
