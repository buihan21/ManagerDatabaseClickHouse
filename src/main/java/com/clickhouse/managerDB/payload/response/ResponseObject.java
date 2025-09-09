package com.clickhouse.managerDB.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
@Builder
@AllArgsConstructor
public class ResponseObject {
    private String message;
    private HttpStatus status;
    private Object data;
}
