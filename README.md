# 配置fusioncompute的zabbix监控

## 环境准备
1. Java1.8+

## zabbix agent安装配置
1. yum安装zabbix agent, 
	yum install /datafs/tomcat/webapps/repo/localmirror/7/os/x86_64/packages/zabbix/zabbix-agent-3.4.4-2.el7.x86_64.rpm

2. 修改zabbix_agentd.conf，文件路径：/etc/zabbix/
	添加如下脚本
	
	Server=[zabbix server ip]
	ServerActive=[zabbix server ip]
	Hostname=[ Hostname of client system ]
	UserParameter=fc.discovery.host[*], /usr/local/jdk1.8.0_51/bin/java -jar /etc/zabbix/fc_zabbix/FC_ZABBIX.jar $1 $2 $3 $4
	UserParameter=fc.discovery.vm[*], /usr/local/jdk1.8.0_51/bin/java -jar /etc/zabbix/fc_zabbix/FC_ZABBIX.jar $1 $2 $3 $4
	UserParameter=fc.monitor.host[*], /usr/local/jdk1.8.0_51/bin/java -jar /etc/zabbix/fc_zabbix/FC_ZABBIX.jar $1 $2 $3 $4 $5 $6
	UserParameter=fc.monitor.vm[*], /usr/local/jdk1.8.0_51/bin/java -jar /etc/zabbix/fc_zabbix/FC_ZABBIX.jar $1 $2 $3 $4 $5 $6
	
3. 将FC_ZABBIX.jar拷贝到/etc/zabbix/fc_zabbix/目录下

4. 重启zabbix agent，service zabbix-agent restart

5. 添加开机启动，chkconfig zabbix-agent on

## zabbix页面操作配置
1. 创建云资源后cop自动创建自发现主机组和主机

2. 按顺序导入fc_monitor_host.xml, fc_monitor_vm.xml, fc_zabbix.xml三个模板

3. 关联fc_zabbix.xml和自动创建的【主机】
