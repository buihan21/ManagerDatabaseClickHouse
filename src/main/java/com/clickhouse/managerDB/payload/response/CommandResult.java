package com.clickhouse.managerDB.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandResult {
    private boolean success;
    private Integer statusCode;
    private String message;
}
