package com.dcits.zabbixagentapiservice.util;

import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;

import java.util.List;

public interface Action {
    String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric);
}