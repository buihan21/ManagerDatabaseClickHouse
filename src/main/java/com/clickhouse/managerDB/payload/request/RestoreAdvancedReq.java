package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class RestoreAdvancedReq {
    private String ipServer;
    private String username;
    private String password;
    private String clickHouseUser;
    private String clickHousePass;
    private String dbName;
    private String tableName;
    private String policyDisk;
    private String remoteFile;
    private String localPath;
}
