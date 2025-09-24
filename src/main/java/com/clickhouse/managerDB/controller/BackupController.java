package com.clickhouse.managerDB.controller;

import com.clickhouse.managerDB.constant.JobType;
import com.clickhouse.managerDB.entity.Job;
import com.clickhouse.managerDB.payload.request.BackupDbReq;
import com.clickhouse.managerDB.payload.response.ResponseObject;
import com.clickhouse.managerDB.service.BackupService;
import com.clickhouse.managerDB.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/backup")
public class BackupController {
    @Autowired
    private BackupService backupService;

    @Autowired
    private JobService  jobService;

//    @PostMapping("/db")
//    public ResponseEntity<ResponseObject> backupToServer(@RequestBody BackupDbReq req) throws Exception {
//        return ResponseEntity.status(HttpStatus.OK).body(
//                ResponseObject.builder()
//                        .message("Backup for DB: " + req.getDbName() + " to " + req.getBackupName())
//                        .status(HttpStatus.OK)
//                        .data(backupService.backupDBtoServer(req.getIpServer(), req.getUsername(), req.getPassword(),
//                                req.getClickHouseUser(), req.getClickHousePass(), req.getDbName(), req.getBackupName(),
//                                req.getLocalCompressedFile()))
//                        .build()
//        );
//    }

    @PostMapping("/db")
    public ResponseEntity<ResponseObject> backupToServer(@RequestBody BackupDbReq req) throws Exception {
        Job job = jobService.createJob(JobType.BACKUP, req.getIpServer(), req.getDbName());
        jobService.submitJob(job.getId(), () -> {
            try {
                backupService.backupDBtoServer(req.getIpServer(), req.getUsername(), req.getPassword(),
                                req.getClickHouseUser(), req.getClickHousePass(), req.getDbName(), req.getBackupName(),
                                req.getLocalCompressedFile()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ResponseObject.builder()
                        .message("Backup DB job submitted")
                        .status(HttpStatus.ACCEPTED)
                        .data(Map.of("jobId", job.getId()))
                        .build()
        );
    }
}
