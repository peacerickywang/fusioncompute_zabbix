#TITLE=zabbixagentapiservice

BASE_HOME=$(cd $(dirname $0) && pwd)
export BASE_HOME
echo "BASE_HOME is "$BASE_HOME

sudo cp -rf application.properties BOOT-INF/classes/application.properties
jar uf zabbix-agent-api-service-0.0.1.jar BOOT-INF/classes/application.properties

nohup ${JAVA_HOME}/bin/java -Dspring.config.location=${BASE_HOME}/application.properties -jar zabbix-agent-api-service-0.0.1.jar > zabbixagentapiservice.log 2>&1 &
