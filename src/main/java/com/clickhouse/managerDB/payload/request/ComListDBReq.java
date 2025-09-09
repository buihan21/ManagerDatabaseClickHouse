package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class ComListDBReq {
    private String ipServer;
    private String username;
    private String password;
    private String clickHouseUser; // user là "default"
    private String clickHousePass;
}
