package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class ComListDiskReq {
    private String ipServer;
    private String username;
    private String password;
}
