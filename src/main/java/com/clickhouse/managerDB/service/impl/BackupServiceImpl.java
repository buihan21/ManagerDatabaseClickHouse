package com.clickhouse.managerDB.service.impl;

import com.clickhouse.managerDB.payload.response.CommandResult;
import com.clickhouse.managerDB.service.BackupService;
import com.clickhouse.managerDB.service.BaseService;
import com.clickhouse.managerDB.service.FTPServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Slf4j
@Service
public class BackupServiceImpl implements BackupService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private FTPServerService ftpServerService;

    @Override
    public String createBackUp(String ip, String username, String password,
                               String clickHouseUser, String clickHousePass,
                               String dbName, String backupName) throws Exception {
        baseService.connect(ip, username,password);
        log.info("Starting create backup DB {} to {}",  dbName, backupName);

        String auth = baseService.authClickHouse(clickHouseUser, clickHousePass);
        String command = String.format("clickhouse-client %s --query=\"BACKUP DATABASE %s TO Disk('backup', '%s')\"",
                auth, dbName, backupName);
        CommandResult commandResult = baseService.executeCommand(command);
        if (!commandResult.isSuccess()) {
            log.error("Failed to create backup DB: {}",  dbName);
            throw new RuntimeException("Failed to create backup for DB: " + dbName + ": " + commandResult.getMessage());
        }
        log.info("Create backup DB {} to {}",  dbName, backupName);

        return String.format("/var/lib/clickhouse/backup/%s", backupName);
    }

    @Override
    public String compressBackup(String ip, String username, String password, String backupName, String targetFile) throws Exception {
        baseService.connect(ip, username,password);
        log.info("Starting compress file backup DB {} to {}",  backupName, targetFile);

        // sudo tar -czf /tmp/backup_db1_v4.tar.gz -C /var/lib/clickhouse/backup backup_db1_v4
        String command = String.format("echo '%s' | sudo -S tar -czf %s -C /var/lib/clickhouse/backup %s", password, targetFile, backupName);
        CommandResult commandResult = baseService.executeCommand(command);
        if (!commandResult.isSuccess()) {
            log.error("Failed to compress file backup DB: {}",  backupName);
            throw new RuntimeException("Failed to compress backup: " + backupName + ": " + commandResult.getMessage());
        }
        log.info("Compress file backup DB {} to {}",  backupName, targetFile);

        return targetFile;
    }

    @Override
    public CommandResult backupDBtoServer(String ip, String username, String password,
                                    String clickHouseUser, String clickHousePass,
                                    String dbName, String backupName,
                                    String localCompressedFile) throws Exception {
        createBackUp(ip, username, password, clickHouseUser, clickHousePass, dbName, backupName);
        //System.out.println(localFile);
        compressBackup(ip, username, password, backupName, localCompressedFile);
        String remoteFTPPath = Paths.get(localCompressedFile).getFileName().toString();
        return ftpServerService.putFileToServer(localCompressedFile, remoteFTPPath);
    }
}
