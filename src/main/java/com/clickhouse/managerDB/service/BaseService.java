package com.clickhouse.managerDB.service;

import com.clickhouse.managerDB.payload.response.CommandResult;

import java.util.List;

public interface BaseService {
    String connect(String ip, String username, String password) throws Exception;
    String disconnect() throws Exception;
    CommandResult executeCommand(String command) throws Exception;
    CommandResult zipFolder(String sourceDir, String outputFile) throws Exception;
    List<String> getListDisk(String ip, String username, String password) throws Exception;
    List<String> getListDB(String ip, String username, String password, String clickHouseUser, String clickHousePass) throws Exception;
    List<String> getListTableOfDB(String ip, String username, String password,
                                  String clickHouseUser, String clickHousePass, String dbName) throws Exception;
    String authClickHouse(String clickHouseUser, String clickHousePass);
    boolean checkExistsFile(String fileName, String folderPath) throws  Exception;
}
