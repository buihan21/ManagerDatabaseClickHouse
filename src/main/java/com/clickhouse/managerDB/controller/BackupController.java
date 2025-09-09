package com.clickhouse.managerDB.controller;

import com.clickhouse.managerDB.payload.request.BackupDbReq;
import com.clickhouse.managerDB.payload.response.ResponseObject;
import com.clickhouse.managerDB.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
public class BackupController {
    @Autowired
    private BackupService backupService;

    @PostMapping("/db")
    public ResponseEntity<ResponseObject> backupToServer(@RequestBody BackupDbReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Backup for DB: " + req.getDbName() + " to " + req.getBackupName())
                        .status(HttpStatus.OK)
                        .data(backupService.backupDBtoServer(req.getIpServer(), req.getUsername(), req.getPassword(),
                                req.getClickHouseUser(), req.getClickHousePass(), req.getDbName(), req.getBackupName(),
                                req.getLocalCompressedFile()))
                        .build()
        );
    }
}
