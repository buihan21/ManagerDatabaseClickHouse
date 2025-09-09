package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class BackupDbReq {
    private String ipServer; //ip của máy tính
    private String username; // của máy tính
    private String password; // của máy tính
    private String clickHouseUser;
    private String clickHousePass;
    private String dbName;
    private String backupName;
    private String localCompressedFile;
    //private String remoteFTPPath;
}
