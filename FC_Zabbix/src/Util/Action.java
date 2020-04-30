package Util;

import java.util.List;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;

public interface Action {
	void doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric);
}
