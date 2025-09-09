package com.clickhouse.managerDB.service;

import com.clickhouse.managerDB.payload.response.CommandResult;

import java.util.List;

public interface FTPServerService {
    CommandResult connect(String ipServer, String username, String password) throws Exception;
    CommandResult putFileToServer(String localPath, String remoteFile) throws Exception;
    CommandResult getFileFromServer(String remoteFile, String localPath) throws Exception;
    List<String> listFileInFTPServer(String ipServer,Integer port, String username, String password) throws Exception;
}
