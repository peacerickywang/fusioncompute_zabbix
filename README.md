# 配置fusioncompute的zabbix监控(新版)

## 环境准备

1. Java1.8+

## zabbix agent安装配置

1. yum安装zabbix agent,

```shell
	yum install /datafs/tomcat/webapps/repo/localmirror/7/os/x86_64/packages/zabbix/zabbix-agent-3.4.4-2.el7.x86_64.rpm
```


​	

2. 修改zabbix_agentd.conf，文件路径：/etc/zabbix/
   添加如下脚本

   ```shell
   Server=[zabbix server ip]
   ServerActive=[zabbix server ip]
   Hostname=[ Hostname of client system ]
   UserParameter=fc.discovery.host[*], curl -s --location --request PUT '192.168.113.12:7000/zabbixagent/discover' --header 'Content-Type: application/json' --data '{"serverIP":"'$1'","username":"'$2'","password":"'$3'","version":"'$5'","action":"'$4'"}'
   UserParameter=fc.discovery.vm[*], curl -s --location --request PUT '192.168.113.12:7000/zabbixagent/discover' --header 'Content-Type: application/json' --data '{"serverIP":"'$1'","username":"'$2'","password":"'$3'","version":"'$5'","action":"'$4'"}'
   UserParameter=fc.monitor.host[*], curl -s --location --request PUT '192.168.113.12:7000/zabbixagent/monitor' --header 'Content-Type: application/json' --data '{"serverIP":"'$1'","username":"'$2'","password":"'$3'","version":"'$5'","action":"'$4'", "instanceName":"'$6'","metric":"'$7'"}'
   UserParameter=fc.monitor.vm[*], curl -s --location --request PUT '192.168.113.12:7000/zabbixagent/monitor' --header 'Content-Type: application/json' --data '{"serverIP":"'$1'","username":"'$2'","password":"'$3'","version":"'$5'","action":"'$4'", "instanceName":"'$6'","metric":"'$7'"}'
   ```

   

3. 修改zabbix_server.conf，文件路径：/etc/zabbix

   ```shell
   Timeout=30
   ```

   

4. 重启zabbix agent

   ```shell
   service zabbix-agent restart
   ```

   

5. 添加开机启动

   ```shell
   chkconfig zabbix-agent on
   ```

## zabbix server配置修改

1. 修改/etc/zabbix/zabbix_server.conf中的CacheSize字段，如果要监控的主机较多，适当调整大该数值，如1024M

## zabbix页面操作配置

1. 导入zbx_export_templates模板
2. 关联fc_zabbix.xml和自动创建的【主机】
3. 配置【主机】中的Macros，注意{$PASSWORD}的值为Base64加密
