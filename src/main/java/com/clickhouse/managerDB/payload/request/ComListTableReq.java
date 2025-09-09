package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class ComListTableReq {
    private String ipServer;
    private String username;
    private String password;
    private String clickHouseUser;
    private String clickHousePass;
    private String dbName;
}
