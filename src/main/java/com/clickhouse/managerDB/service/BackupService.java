package com.clickhouse.managerDB.service;

import com.clickhouse.managerDB.payload.response.CommandResult;

public interface BackupService {
    String createBackUp(String ip, String username, String password,
                        String clickHouseUser, String clickHousePass,
                        String dbName, String backupName) throws Exception;
    String compressBackup(String ip, String username, String password, String backupName, String targetFile) throws Exception;
    CommandResult backupDBtoServer(String ip, String username, String password,
                                String clickHouseUser, String clickHousePass,
                                String dbName, String backupName,
                                String localCompressedFile) throws Exception;
}
