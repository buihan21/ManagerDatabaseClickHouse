package com.clickhouse.managerDB.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "ftp")
public class FTPConfig {
    private String ipServer;
    private String user;
    private String pass;
}
