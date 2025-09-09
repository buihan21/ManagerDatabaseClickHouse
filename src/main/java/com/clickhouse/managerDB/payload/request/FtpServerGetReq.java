package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class FtpServerGetReq {
//    private String ipServer;
//    private String username;
//    private String password;
    private String remoteFile;
    private String localPath;
}
