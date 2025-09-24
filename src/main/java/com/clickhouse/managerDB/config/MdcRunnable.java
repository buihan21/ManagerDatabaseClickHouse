package com.clickhouse.managerDB.config;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class MdcRunnable implements  Runnable {
    private final Runnable runnable;
    private final Map<String, String> contextMap;

    public MdcRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.contextMap = MDC.getCopyOfContextMap() != null ? new HashMap<>(MDC.getCopyOfContextMap()) : new HashMap<>();
    }
    @Override
    public void run() {
        try {
            MDC.setContextMap(contextMap);
            runnable.run();
        } finally {
            MDC.clear();
        }
    }

}
