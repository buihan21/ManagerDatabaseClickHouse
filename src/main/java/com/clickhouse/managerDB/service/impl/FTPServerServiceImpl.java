package com.clickhouse.managerDB.service.impl;

import com.clickhouse.managerDB.config.FTPConfig;
import com.clickhouse.managerDB.payload.response.CommandResult;
import com.clickhouse.managerDB.service.BaseService;
import com.clickhouse.managerDB.service.FTPServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FTPServerServiceImpl implements FTPServerService {
    @Autowired
    private BaseService  baseService;
    @Autowired
    private FTPConfig ftpConfig;

    @Override
    public CommandResult connect(String ipServer, String username, String password) throws Exception {
        String command = String.format("ftp -inv %s <<EOF\nuser %s %s\nbye\nEOF",  ipServer, username, password);
        CommandResult commandResult = baseService.executeCommand(command);
        if (commandResult.getStatusCode() != 0) {
            return commandResult;
        } else {
            String output = commandResult.getMessage();
            if (output.contains("230")) {
                return new CommandResult(true, 0, "FTP connect success\n" + output);
            } else if (output.contains("530")) {
                return new CommandResult(false, 1, "FTP connect failed: wrong username/password\n" + output);
            } else {
                return new CommandResult(false, 1, "FTP connect unknown error\n" + output);
            }
        }
    }

    @Override
    public CommandResult putFileToServer(String localPath, String remoteFile) throws  Exception {
        //baseService.connect(ipServer, username, password);
        //connect(ftpConfig.getIpServer(), ftpConfig.getUser(), ftpConfig.getPass());
        log.info(" Starting put file {} to server", localPath);

        String command = String.format("echo -e \"user %s %s\\nput %s %s\\nbye\" | ftp -inv %s",
                ftpConfig.getUser(), ftpConfig.getPass(), localPath, remoteFile, ftpConfig.getIpServer());
        CommandResult commandResult = baseService.executeCommand(command);
        if (commandResult.getStatusCode() != 0) {
            log.error("Failed to put file {} to server", localPath);
            return commandResult;
        } else {
            String output = commandResult.getMessage();
            if (output.contains("226")) {
                log.info("Put file {} to server successful", localPath);
                return new CommandResult(true, 0, "Put file success\n" + output);
            } else if (output.contains("550")) {
                log.error("Failed to put file {} to server", localPath);
                return new CommandResult(false, 1, "Put file failed\n" + output);
            } else {
                log.error("Failed to put file {} to server", localPath);
                return new CommandResult(false, 1, "Put file unknown error\n" + output);
            }
        }
    }

    @Override
    public CommandResult getFileFromServer(String remoteFile, String localPath) throws Exception{
        log.info(" Starting get file {} from server to localPath {}", remoteFile, localPath);

        if (localPath == null || localPath.isEmpty()) {
            localPath = "/tmp/get-file/" + remoteFile;
        }
        String command = String.format("echo -e \"user %s %s\\nget %s %s\\nbye\" | ftp -inv %s",
                ftpConfig.getUser(), ftpConfig.getPass(), remoteFile, localPath, ftpConfig.getIpServer());
        CommandResult commandResult = baseService.executeCommand(command);
        if (commandResult.getStatusCode() != 0) {
            log.error("Failed to get file {} from server to localPath {}", remoteFile, localPath);
            return commandResult;
        } else {
            String output = commandResult.getMessage();
            if (output.contains("226")) {
                log.info("Get file {} from server successful", localPath);
                return new CommandResult(true, 0, "Get file success\n" + output);
            } else if (output.contains("550")) {
                log.error("Failed to get file {} from server to localPath {}", remoteFile, localPath);
                return new CommandResult(false, 1, "Get file failed\n" + output);
            } else  {
                log.error("Failed to get file {} from server to localPath {}", remoteFile, localPath);
                return new CommandResult(false, 1, "Get file unknown error\n" + output);
            }
        }
    }

    @Override
    public List<String> listFileInFTPServer(String ipServer, Integer port, String username, String password) throws Exception {
        String command = String.format("ftp -inv %s %d <<EOF\nuser %s %s\nls\nbye\nEOF",   ipServer, port, username, password);
        CommandResult commandResult = baseService.executeCommand(command);
        if (!commandResult.isSuccess()) {
            throw new RuntimeException("Failed to list file in FTP server" + commandResult.getMessage());
        }
        List<String> files = new ArrayList<>();
        for (String line : commandResult.getMessage().split("\n")) {
            if (line.startsWith("-") || line.startsWith("d")) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length > 0) {
                    files.add(parts[parts.length - 1]);
                }
            }
        }
        return files;
    }
}
