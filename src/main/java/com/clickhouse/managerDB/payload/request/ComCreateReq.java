package com.clickhouse.managerDB.payload.request;

import lombok.Getter;

@Getter
public class ComCreateReq {
    private String name;
    private String ipAddress;
    private Integer port;
    private String location;
    private String username;
    private String password;
    private String clickHouseUser;
    private String clickHousePass;
}
