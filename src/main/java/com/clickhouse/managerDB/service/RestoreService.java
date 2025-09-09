package com.clickhouse.managerDB.service;

import com.clickhouse.managerDB.payload.response.CommandResult;

public interface RestoreService {
    String decompressBackup(String ip, String username, String password, String backupFile) throws Exception;
    CommandResult restoreDatabase(String ip, String username, String password,
                                  String clickHouseUser, String clickHousePass,
                                  String dbName, String remoteFile, String localPath) throws Exception;
    CommandResult restoreTable(String ip, String username, String password,
                               String clickHouseUser, String clickHousePass,
                               String dbName, String tableName,
                               String remoteFile, String localPath) throws Exception;
    CommandResult restoreAdvanced(String ip, String username, String password,
                                  String clickHouseUser, String clickHousePass,
                                  String dbName, String tableName, String policyDisk,
                                  String remoteFile, String localPath) throws Exception;
}
