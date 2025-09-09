package com.clickhouse.managerDB.controller;

import com.clickhouse.managerDB.payload.request.ComListDBReq;
import com.clickhouse.managerDB.payload.request.ComListDiskReq;
import com.clickhouse.managerDB.payload.request.ComListTableReq;
import com.clickhouse.managerDB.payload.request.SshToComputerReq;
import com.clickhouse.managerDB.payload.response.ResponseObject;
import com.clickhouse.managerDB.service.BaseService;
import com.clickhouse.managerDB.service.ComputerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/computer")
public class ComputerController {
    @Autowired
    private ComputerService computerService;

    @Autowired
    private BaseService baseService;

    @GetMapping("/")
    public ResponseEntity<ResponseObject> getAllComputers() throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get all computers")
                        .status(HttpStatus.OK)
                        .data(computerService.getAllComputers())
                        .build()
        );
    }

    @GetMapping("/getOne")
    public ResponseEntity<ResponseObject> getOneComputer(@RequestParam String ipAddress) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get computer have IP: " + ipAddress)
                        .status(HttpStatus.OK)
                        .data(computerService.getComputerByIp(ipAddress))
                        .build()
        );
    }

    @PostMapping("/connect")
    public ResponseEntity<ResponseObject> sshToComputer(@RequestBody SshToComputerReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("SSH computer successfully")
                        .status(HttpStatus.OK)
                        .data(baseService.connect(req.getIpAddress(), req.getUsername(), req.getPassword()))
                        .build()
        );
    }

    @GetMapping("/disks")
    public ResponseEntity<ResponseObject> getListDisk(@RequestBody ComListDiskReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get list disk on ip: " + req.getIpServer())
                        .status(HttpStatus.OK)
                        .data(baseService.getListDisk(req.getIpServer(), req.getUsername(), req.getPassword()))
                        .build()
        );
    }

    @GetMapping("/database")
    public ResponseEntity<ResponseObject> getListDB(@RequestBody ComListDBReq req)  throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get list database on ip: " + req.getIpServer())
                        .status(HttpStatus.OK)
                        .data(baseService.getListDB(req.getIpServer(), req.getUsername(), req.getPassword(),
                                req.getClickHouseUser(), req.getClickHousePass()))
                        .build()
        );
    }
    @GetMapping("/database/tables")
    public ResponseEntity<ResponseObject> getListTableOfDB(@RequestBody ComListTableReq req)  throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Get tables of DB: " + req.getDbName())
                        .status(HttpStatus.OK)
                        .data(baseService.getListTableOfDB(req.getIpServer(), req.getUsername(), req.getPassword(),
                                req.getClickHouseUser(), req.getClickHousePass(), req.getDbName()))
                        .build()
        );
    }
}
