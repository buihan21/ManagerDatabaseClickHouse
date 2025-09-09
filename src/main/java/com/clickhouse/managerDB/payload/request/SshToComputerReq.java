package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class SshToComputerReq {
    private String ipAddress;
    private String username;
    private String password;
}
