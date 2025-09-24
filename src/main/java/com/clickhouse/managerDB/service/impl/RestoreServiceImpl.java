package com.clickhouse.managerDB.service.impl;

import com.clickhouse.managerDB.payload.response.CommandResult;
import com.clickhouse.managerDB.service.BaseService;
import com.clickhouse.managerDB.service.FTPServerService;
import com.clickhouse.managerDB.service.RestoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
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
        log.info("Start restore database {}", dbName);
        // get file backup từ ftp server
        if (baseService.checkExistsFile(remoteFile, localPath)) {
            CommandResult commandResult = ftpServerService.getFileFromServer(remoteFile, localPath);
            if (commandResult.getStatusCode() != 0) {
                log.error("Did not get file {} from server", remoteFile);
                throw new RuntimeException("Did not get file" + remoteFile+ "from server");
                //return new CommandResult(false, 1, "restore DB failed because did not get file from FTP server");
            }
        }

        log.info("Get file {} from server", remoteFile);
        // giải nén file
        decompressBackup(ip, username, password, localPath);
        log.info("Decompressed backup file {}", localPath);

        // drop db
        String auth = baseService.authClickHouse(clickHouseUser, clickHousePass);
        String dropCmd = String.format("clickhouse-client %s --query=\"DROP DATABASE IF EXISTS %s\"",auth, dbName);
        CommandResult dropResult = baseService.executeCommand(dropCmd);
        if (!dropResult.isSuccess()){
            log.error("Failed to drop DB {}", dbName);
            throw new RuntimeException("Failed to drop DB: " + dbName + "\n" + dropResult.getMessage());
        }

        log.info("Dropped database {} successful", dbName);
        // restore db
        String backupName = remoteFile.split("\\.")[0];
        String restoreCmd = String.format("clickhouse-client %s --query=\"RESTORE DATABASE %s FROM Disk('backup', '%s')\"",
                auth, dbName, backupName);
        CommandResult restoreResult = baseService.executeCommand(restoreCmd);
        if (!restoreResult.isSuccess()){
            log.error("Failed to restore DB {}", dbName);
            throw new RuntimeException("Failed to restore DB: " + dbName + "\n" + restoreResult.getMessage());
        }
        log.info("Restored DB {} successful", dbName);
        return new CommandResult(true, 0, "restore DB successful");
    }

    @Override
    public CommandResult restoreTable(String ip, String username, String password,
                                      String clickHouseUser, String clickHousePass,
                                      String dbName, String tableName,
                                      String remoteFile, String localPath) throws Exception {
        baseService.connect(ip, username, password);
        log.info("Start restore table {}.{}", dbName, tableName);
        // get file từ ftp server
        if (baseService.checkExistsFile(remoteFile, localPath)) {
            CommandResult commandResult = ftpServerService.getFileFromServer(remoteFile, localPath);
            if (commandResult.getStatusCode() != 0) {
                log.error("Did not get file {} from server", remoteFile);
                throw new RuntimeException("Did not get file" + remoteFile + "from FTP server");
                //return new CommandResult(false, 1, "restore Table failed because did not get file from FTP server");
            }
        }

//        CommandResult commandResult = ftpServerService.getFileFromServer(remoteFile, localPath);
//        if (commandResult.getStatusCode() != 0) {
//            log.error("Did not get file {} from server", remoteFile);
//            throw new RuntimeException("Did not get file" + remoteFile + " from FTP server");
//            //return new CommandResult(false, 1, "restore Table failed because did not get file from FTP server");
//        }
        log.info("Get file {} from server", remoteFile);
        // giải nén file
        //localPath = "/tmp/get-file/" + remoteFile;
        decompressBackup(ip, username, password, localPath);
        log.info("Decompressed backup file {}",  localPath);
        String auth = baseService.authClickHouse(clickHouseUser, clickHousePass);
        String dropCmd = String.format("clickhouse-client %s --query=\"DROP TABLE IF EXISTS %s.%s\"",auth,  dbName, tableName);
        CommandResult dropResult = baseService.executeCommand(dropCmd);
        if (!dropResult.isSuccess()){
            log.error("Failed to drop table {}.{}", dbName, tableName);
            throw new RuntimeException("Failed to drop Table: " + dbName + "." + tableName + "\n" + dropResult.getMessage());
        }
        log.info("Dropped table {}.{} successful", dbName, tableName);
        // restore table
        String backupName = remoteFile.split("\\.")[0];
        String restoreCmd = String.format("clickhouse-client %s --query=\"RESTORE TABLE %s.%s FROM Disk('backup', '%s')\"",
                auth, dbName, tableName, backupName);
        CommandResult restoreResult = baseService.executeCommand(restoreCmd);
        if (!restoreResult.isSuccess()){
            log.error("Failed to restore table {}.{}", dbName, tableName);
            throw new RuntimeException("Failed to restore Table: " + dbName + "." + tableName + "\n" + restoreResult.getMessage());
        }
        log.info("Restored table {}.{} successful", dbName, tableName);
        return new CommandResult(true, 0, "restore Table successful");
    }

    @Override
    public CommandResult restoreAdvanced(String ip, String username, String password,
                                         String clickHouseUser, String clickHousePass,
                                         String dbName, String tableName, String policyDisk,
                                         String remoteFile, String localPath) throws Exception {
        baseService.connect(ip, username, password);
        log.info("Start restore advanced {}.{}", dbName, tableName);

        // get file từ ftp server
        if (baseService.checkExistsFile(remoteFile, localPath)) {
            CommandResult commandResult = ftpServerService.getFileFromServer(remoteFile, localPath);
            if (commandResult.getStatusCode() != 0) {
                log.error("Did not get file {} from server", remoteFile);
                throw new RuntimeException("Did not get file " + remoteFile + " from FTP server");
                //return new CommandResult(false, 1, "restore Table failed because did not get file from FTP server");
            }
        }
        log.info("Get file {} from server", remoteFile);

        // giải nén file
        decompressBackup(ip, username, password, localPath);
        log.info("Decompressed backup file {}",  localPath);

        String auth = baseService.authClickHouse(clickHouseUser, clickHousePass);
        String dropCmd = String.format("clickhouse-client %s --query=\"DROP TABLE IF EXISTS %s.%s\"",auth,  dbName, tableName);
        CommandResult dropResult = baseService.executeCommand(dropCmd);
        if (!dropResult.isSuccess()){
            log.error("Failed to drop table {}.{}", dbName, tableName);
            throw new RuntimeException("Failed to drop Table: " + dbName + "." + tableName + "\n" + dropResult.getMessage());
        }
        log.info("Dropped table {}.{} successful", dbName, tableName);

        // restore table vào disk mới (khác policy ban đầu)
        String backupName = remoteFile.split("\\.")[0];
        String restoreCmd = String.format("clickhouse-client %s --query=\"RESTORE TABLE %s.%s FROM Disk('backup', '%s') SETTINGS storage_policy='%s'\"",
                auth, dbName, tableName, backupName, policyDisk);
        CommandResult restoreResult = baseService.executeCommand(restoreCmd);
        if (!restoreResult.isSuccess()){
            log.error("Failed to restore table {}.{}", dbName, tableName);
            throw new RuntimeException("Failed to restore Table: " + dbName + "." + tableName + "\n" + restoreResult.getMessage());
        }
        log.info("Restored table {}.{} successful", dbName, tableName);

        return new CommandResult(true, 0, "restore Table successful");
    }
}
