package com.clickhouse.managerDB.service.impl;

import com.clickhouse.managerDB.payload.response.CommandResult;
import com.clickhouse.managerDB.service.BaseService;
import com.clickhouse.managerDB.service.FTPServerService;
import com.clickhouse.managerDB.service.RestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestoreServiceImpl implements RestoreService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private FTPServerService  ftpServerService;

    //Giải nén file
    @Override
    public String decompressBackup(String ip, String username, String password, String filePath) throws Exception {
        baseService.connect(ip, username, password);
        String command = String.format("echo '%s' | sudo -S tar -xzf %s -C /var/lib/clickhouse/backup", password, filePath);
        CommandResult  commandResult = baseService.executeCommand(command);
        if (!commandResult.isSuccess()){
            throw new RuntimeException("Failed to decompress backup: " + filePath + ": " + commandResult.getMessage());
        }
        return filePath.split("\\.")[0];
    }

    @Override
    public CommandResult restoreDatabase(String ip, String username, String password,
                                         String clickHouseUser, String clickHousePass,
                                         String dbName, String remoteFile, String localPath) throws Exception {
        baseService.connect(ip, username, password);
        // get file backup từ ftp server
        if (baseService.checkExistsFile(remoteFile, localPath)) {
            CommandResult commandResult = ftpServerService.getFileFromServer(remoteFile, localPath);
            if (commandResult.getStatusCode() != 0) {
                return new CommandResult(false, 1, "restore DB failed because did not get file from FTP server");
            }
        }

        // giải nén file
        decompressBackup(ip, username, password, localPath);
        String auth = baseService.authClickHouse(clickHouseUser, clickHousePass);
        // drop db
        String dropCmd = String.format("clickhouse-client %s --query=\"DROP DATABASE IF EXISTS %s\"",auth, dbName);
        CommandResult dropResult = baseService.executeCommand(dropCmd);
        if (!dropResult.isSuccess()){
            throw new RuntimeException("Failed to drop DB: " + dbName + "\n" + dropResult.getMessage());
        }

        // restore db
        String backupName = remoteFile.split("\\.")[0];
        String restoreCmd = String.format("clickhouse-client %s --query=\"RESTORE DATABASE %s FROM Disk('backup', '%s')\"",
                auth, dbName, backupName);
        CommandResult restoreResult = baseService.executeCommand(restoreCmd);
        if (!restoreResult.isSuccess()){
            throw new RuntimeException("Failed to restore DB: " + dbName + "\n" + restoreResult.getMessage());
        }
        return new CommandResult(true, 0, "restore DB successful");
    }

    @Override
    public CommandResult restoreTable(String ip, String username, String password,
                                      String clickHouseUser, String clickHousePass,
                                      String dbName, String tableName,
                                      String remoteFile, String localPath) throws Exception {
        baseService.connect(ip, username, password);
        // get file từ ftp server
        if (baseService.checkExistsFile(remoteFile, localPath)) {
            CommandResult commandResult = ftpServerService.getFileFromServer(remoteFile, localPath);
            if (commandResult.getStatusCode() != 0) {
                return new CommandResult(false, 1, "restore Table failed because did not get file from FTP server");
            }
        }
        // giải nén file
        decompressBackup(ip, username, password, localPath);
        String auth = baseService.authClickHouse(clickHouseUser, clickHousePass);
        String dropCmd = String.format("clickhouse-client %s --query=\"DROP TABLE IF EXISTS %s.%s\"",auth,  dbName, tableName);
        CommandResult dropResult = baseService.executeCommand(dropCmd);
        if (!dropResult.isSuccess()){
            throw new RuntimeException("Failed to drop Table: " + dbName + "." + tableName + "\n" + dropResult.getMessage());
        }
        // restore table
        String backupName = remoteFile.split("\\.")[0];
        String restoreCmd = String.format("clickhouse-client %s --query=\"RESTORE TABLE %s.%s FROM Disk('backup', '%s')\"",
                auth, dbName, tableName, backupName);
        CommandResult restoreResult = baseService.executeCommand(restoreCmd);
        if (!restoreResult.isSuccess()){
            throw new RuntimeException("Failed to restore Table: " + dbName + "." + tableName + "\n" + restoreResult.getMessage());
        }
        return new CommandResult(true, 0, "restore Table successful");
    }

    @Override
    public CommandResult restoreAdvanced(String ip, String username, String password,
                                         String clickHouseUser, String clickHousePass,
                                         String dbName, String tableName, String policyDisk,
                                         String remoteFile, String localPath) throws Exception {
        baseService.connect(ip, username, password);
        // get file từ ftp server
        if (baseService.checkExistsFile(remoteFile, localPath)) {
            CommandResult commandResult = ftpServerService.getFileFromServer(remoteFile, localPath);
            if (commandResult.getStatusCode() != 0) {
                return new CommandResult(false, 1, "restore Table failed because did not get file from FTP server");
            }
        }
        // giải nén file
        decompressBackup(ip, username, password, localPath);
        String auth = baseService.authClickHouse(clickHouseUser, clickHousePass);
        String dropCmd = String.format("clickhouse-client %s --query=\"DROP TABLE IF EXISTS %s.%s\"",auth,  dbName, tableName);
        CommandResult dropResult = baseService.executeCommand(dropCmd);
        if (!dropResult.isSuccess()){
            throw new RuntimeException("Failed to drop Table: " + dbName + "." + tableName + "\n" + dropResult.getMessage());
        }
        // restore table vào disk mới (khác policy ban đầu)
        String backupName = remoteFile.split("\\.")[0];
        String restoreCmd = String.format("clickhouse-client %s --query=\"RESTORE TABLE %s.%s FROM Disk('backup', '%s') SETTINGS storage_policy='%s'\"",
                auth, dbName, tableName, backupName, policyDisk);
        CommandResult restoreResult = baseService.executeCommand(restoreCmd);
        if (!restoreResult.isSuccess()){
            throw new RuntimeException("Failed to restore Table: " + dbName + "." + tableName + "\n" + restoreResult.getMessage());
        }
//        //step1: tạo lại schema (
//        String backupName = remoteFile.split("\\.")[0];
//        String createSchemaCmd = String.format("clickhouse-client %s --query=\"" +
//                        "RESTORE TABLE %s.%s FROM Disk('backup', '%s') SETTINGS structure_only = true\"",
//                auth, dbName, tableName, backupName);
//        CommandResult createSchemaResult = baseService.executeCommand(createSchemaCmd);
//        if (!createSchemaResult.isSuccess()){
//            throw new RuntimeException("Failed to create Table: " + dbName + "." + tableName + "\n" + createSchemaResult.getMessage());
//        }
//        // step2: đổi policy sang disk mới
//        String changeStoragePolicyCmd = String.format("clickhouse-client %s --query=\"ALTER TABLE %s.%s MODIFY SETTING storage_policy = '%s'\"",
//                auth, dbName, tableName, policyDisk);
//        CommandResult changeStoragePolicyResult = baseService.executeCommand(changeStoragePolicyCmd);
//        if (!changeStoragePolicyResult.isSuccess()){
//            throw new RuntimeException("Failed to change storage policy: " + dbName + "." + tableName + "\n" + changeStoragePolicyResult.getMessage());
//        }
//        //step3: restore lại data
//        String restoreCmd = String.format("clickhouse-client %s --query=\"RESTORE TABLE %s.%s FROM Disk('backup', '%s') SETTINGS data_only = true\"",
//                auth, dbName, tableName, backupName);
//        CommandResult restoreResult = baseService.executeCommand(restoreCmd);
//        if (!restoreResult.isSuccess()){
//            throw new RuntimeException("Failed to restore Table: " + dbName + "." + tableName + "\n" + restoreResult.getMessage());
//        }
        return new CommandResult(true, 0, "restore Table successful");
    }
}
