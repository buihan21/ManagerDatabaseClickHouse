package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class RestoreDbReq {
    private String ipServer;
    private String username;
    private String password;
    private String clickHouseUser;
    private String clickHousePass;
    //private String backupFile;
    private String dbName;
    private String remoteFile;
    private String localPath;
}
