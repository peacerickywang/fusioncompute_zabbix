package main;

import java.util.Base64;
import java.util.List;
import com.huawei.esdk.fusioncompute.local.ServiceFactory;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.FCSDKResponse;
import com.huawei.esdk.fusioncompute.local.model.common.LoginResp;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.resources.common.AuthenticateResource;
import com.huawei.esdk.fusioncompute.local.resources.site.SiteResource;
import Util.Action;
import Util.ActionFactory;


public class main {
	
	/**
     * 服务器IP地址
     */
//    public static String serverIP = "10.0.202.94";
    
    /**
     * 服务器端口号
     */
    public static String serverPort = "7443";
    
    /**
     * 登录所用的用户名
     */
//    public static String userName = "wangrs";
    
    /**
     * 登录用户的密码
     */
//    public static String password = "P@ssw0rd";
    
    /**
     * 错误码
     */
    public static String ERROR_CODE = "00000000";
    

	public static void main(String[] args) {
		//server IP
		String serverIP = args[0];
		//登录所用的用户名
		String username = args[1];
		//登录用户的密码
		String password = args[2];
		//操作
		String ACTION = args[3];
		// 设定服务器配置
		ClientProviderBean clientProvider = new ClientProviderBean();
		// 设定服务器配置_设定服务器IP
		clientProvider.setServerIp(serverIP);
		// 设定服务器配置_设定服务器端口号
		clientProvider.setServerPort(serverPort);
		clientProvider.setVersion(Float.parseFloat("6.3"));
		// 初始化用户资源实例
		AuthenticateResource auth = ServiceFactory.getService(AuthenticateResource.class, clientProvider);
		// 以用户名，用户密码作为传入参数，调用AuthenticateResource提供的login方法，完成用户的登录
		try {
			FCSDKResponse<LoginResp> resp = auth.login(username,new String(Base64.getDecoder().decode(password),"utf-8"));
			if (!resp.getErrorCode().equals("00000000")) {
				throw new Exception("登录失败，服务器连接认证失败！");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// 获取SiteResource接口的实现
		SiteResource site = ServiceFactory.getService(SiteResource.class, clientProvider);
		FCSDKResponse<List<SiteBasicInfo>> resps = site.querySites();
		if (!resps.getErrorCode().equals("00000000")) {
			System.out.println("获取站点信息失败！");
		}
		List<SiteBasicInfo> siteBasicInfoList = resps.getResult();

		Action targetAction = ActionFactory.getAction(ACTION).orElseThrow(() -> new IllegalArgumentException("Invalid Action"));
		targetAction.doAction(clientProvider, siteBasicInfoList, String.valueOf(args[args.length-2]), String.valueOf(args[args.length-1]));
	}

}
