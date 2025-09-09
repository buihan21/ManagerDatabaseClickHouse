package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class FtpServerPutReq {
//    private String ipServer;
//    private String username;
//    private String password;
    private String localPath;
    private String remoteFile;
}
