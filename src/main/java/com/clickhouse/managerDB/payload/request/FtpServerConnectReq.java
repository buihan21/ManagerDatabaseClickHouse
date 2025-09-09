package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class FtpServerConnectReq {
    private String ipServer;
    private String username;
    private String password;
}
