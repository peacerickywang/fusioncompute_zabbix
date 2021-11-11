#!/bin/bash
pn=`ps -ef|grep -i "zabbix-agent-api-service-0.0.1.jar"|grep -v grep|awk '{print $2}'`
if [ -n "$pn" ];then
echo "killing smartcop process,excute kill -9 $pn"

kill -9 $pn

fi
