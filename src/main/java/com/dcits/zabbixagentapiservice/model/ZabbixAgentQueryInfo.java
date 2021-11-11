package com.dcits.zabbixagentapiservice.model;

import lombok.Data;

@Data
public class ZabbixAgentQueryInfo {
    //server IP
    String serverIP;
    //登录所用的用户名
    String username;
    //登录用户的密码
    String password;
    //登录用户的密码
    String version;
    //操作
    String action;
    //实例名称
    String instanceName;
    //监控项
    String metric;
}
