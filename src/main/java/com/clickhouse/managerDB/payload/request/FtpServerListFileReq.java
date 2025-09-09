package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class FtpServerListFileReq {
    private String ipServer;
    private Integer port; // mặc định port 21
    private String username;
    private String password;
}
