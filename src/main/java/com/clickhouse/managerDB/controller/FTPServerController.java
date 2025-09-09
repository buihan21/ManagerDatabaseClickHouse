package com.clickhouse.managerDB.controller;

import com.clickhouse.managerDB.payload.request.FtpServerConnectReq;
import com.clickhouse.managerDB.payload.request.FtpServerGetReq;
import com.clickhouse.managerDB.payload.request.FtpServerListFileReq;
import com.clickhouse.managerDB.payload.request.FtpServerPutReq;
import com.clickhouse.managerDB.payload.response.ResponseObject;
import com.clickhouse.managerDB.service.FTPServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ftpServer")
public class FTPServerController {
    @Autowired
    private FTPServerService ftpServerService;

    @PostMapping("/connect")
    public ResponseEntity<ResponseObject> testConnection(@RequestBody FtpServerConnectReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Test connect to FTP server")
                        .status(HttpStatus.OK)
                        .data(ftpServerService.connect(req.getIpServer(), req.getUsername(), req.getPassword()))
                        .build()
        );
    }

    @PostMapping("/put")
    public ResponseEntity<ResponseObject> testPutFile(@RequestBody FtpServerPutReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Test put file to FTP server")
                        .status(HttpStatus.OK)
                        .data(ftpServerService.putFileToServer(req.getLocalPath(), req.getRemoteFile()))
                        .build()
        );
    }

    @PostMapping("/get")
    public ResponseEntity<ResponseObject> testGetFile(@RequestBody FtpServerGetReq  req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Test get file from FTP server")
                        .status(HttpStatus.OK)
                        .data(ftpServerService.getFileFromServer(req.getRemoteFile(), req.getLocalPath()))
                        .build()
        );
    }

    @GetMapping("/listFile")
    public ResponseEntity<ResponseObject> listFileInFTPServer(@RequestBody FtpServerListFileReq req) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("List file in FTP server")
                        .status(HttpStatus.OK)
                        .data(ftpServerService.listFileInFTPServer(req.getIpServer(), 21, req.getUsername(), req.getPassword()))
                        .build()
        );
    }
}
