package com.clickhouse.managerDB.controller;

import com.clickhouse.managerDB.payload.request.RestoreAdvancedReq;
import com.clickhouse.managerDB.payload.request.RestoreDbReq;
import com.clickhouse.managerDB.payload.request.RestoreTableReq;
import com.clickhouse.managerDB.payload.response.ResponseObject;
import com.clickhouse.managerDB.service.RestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restore")
public class RestoreController {
    @Autowired
    private RestoreService restoreService;

    @PostMapping("/db")
    public ResponseEntity<ResponseObject> restoreDB(@RequestBody RestoreDbReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Restore DB " + req.getDbName() + " from " + req.getRemoteFile())
                        .status(HttpStatus.OK)
                        .data(restoreService.restoreDatabase(req.getIpServer(), req.getUsername(), req.getPassword(),
                                req.getClickHouseUser(), req.getClickHousePass(), req.getDbName(),
                                req.getRemoteFile(), req.getLocalPath()))
                        .build()
        );
    }

    @PostMapping("/table")
    public ResponseEntity<ResponseObject> responseTable(@RequestBody RestoreTableReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Restore Table " + req.getTableName() + " from " + req.getRemoteFile())
                        .status(HttpStatus.OK)
                        .data(restoreService.restoreTable(req.getIpServer(), req.getUsername(), req.getPassword(),
                                req.getClickHouseUser(), req.getClickHousePass(), req.getDbName(), req.getTableName(),
                                req.getRemoteFile(), req.getLocalPath()))
                        .build()
        );
    }

    @PostMapping("/advanced")
    public ResponseEntity<ResponseObject> restoreTableAdvanced(@RequestBody RestoreAdvancedReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Restore Advanced " + req.getTableName() + " from " + req.getRemoteFile() + " to " + req.getPolicyDisk())
                        .status(HttpStatus.OK)
                        .data(restoreService.restoreAdvanced(req.getIpServer(), req.getUsername(), req.getPassword(),
                                req.getClickHouseUser(), req.getClickHousePass(), req.getDbName(), req.getTableName(),
                                req.getPolicyDisk(),  req.getRemoteFile(), req.getLocalPath()))
                        .build()
        );
    }
}
