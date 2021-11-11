package com.dcits.zabbixagentapiservice.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ActionFactory {
    static Map<String, Action> actionMap = new HashMap<>();

    static {
        actionMap.put("discover_host", new DiscoverHosts());
        actionMap.put("discover_vm", new DiscoverVMs());
        actionMap.put("monitor_vm", new MonitorVM());
        actionMap.put("monitor_host", new MonitorHost());
    }

    public static Optional<Action> getAction (String action){
        return Optional.ofNullable(actionMap.get(action));
    }
}
