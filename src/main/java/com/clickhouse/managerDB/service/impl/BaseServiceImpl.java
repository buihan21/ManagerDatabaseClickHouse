package com.clickhouse.managerDB.service.impl;

import com.clickhouse.managerDB.payload.response.CommandResult;
import com.clickhouse.managerDB.service.BaseService;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BaseServiceImpl implements BaseService {
    private SSHClient sshClient;
    //private Session session;

    @Override
    public String connect(String ip, String username, String password) throws Exception {
        if (sshClient != null && sshClient.isConnected()) {
            System.out.println("Already connected to :" + ip);
            return "Already connected to :" + ip;
        }

        try {
            sshClient = new SSHClient();
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(ip);

            sshClient.authPassword(username, password);
            System.out.println("Connected to :" + ip);
            return "Connected to :" + ip;
        } catch (IOException e) {
            throw new RuntimeException("SSH connection failed", e);
        }
    }

    @Override
    public String disconnect() throws Exception {
        try {
            if (sshClient != null && sshClient.isConnected()) {
                sshClient.disconnect();
                System.out.println("Disconnected from " + sshClient);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("SSH disconnection failed", ioe);
        }
        return "SSH disconnected from :" + sshClient;
    }

    @Override
    public CommandResult executeCommand(String command) throws Exception {
        try (Session session = sshClient.startSession()) {
            try (Session.Command cmd = session.exec(command)) {
                cmd.join(20, TimeUnit.SECONDS); // chờ tối đa 20s
                Integer exitCode = cmd.getExitStatus();
                String error = IOUtils.readFully(cmd.getErrorStream()).toString();
                String output = IOUtils.readFully(cmd.getInputStream()).toString();
                System.out.println("====EXIT CODE==== : " + exitCode);
                if (exitCode == 0 && exitCode != null) {
                    return new CommandResult(true, exitCode, output.trim());
                } else {
                    return new CommandResult(false, exitCode, error.trim());
                }
            }
        }
    }

    @Override
    public CommandResult zipFolder(String sourceDir, String outputFile) throws Exception {
        java.io.File zipFolder = new java.io.File(sourceDir);
        String parent = zipFolder.getParent();
        String name = zipFolder.getName();
        String cmd = String.format("tar -czvf %s -C %s %s", outputFile, parent, name);
        return executeCommand(cmd);
    }

    @Override
    public List<String> getListDisk(String ip, String username, String password) throws Exception {
        connect(ip, username, password);

        String command = "lsblk -dn -o NAME";
        CommandResult commandResult = executeCommand(command);
        if (!commandResult.isSuccess()) {
            throw new RuntimeException("Failed to get list disk: \n" + commandResult.getMessage());
        }
        List<String> disks = new ArrayList<>();
        for (String line : commandResult.getMessage().split("\n")) {
            if (!line.trim().isEmpty()) {
                disks.add(line.trim());
            }
        }
        return disks;
    }

    @Override
    public List<String> getListDB(String ip, String username, String password,
                                  String clickHouseUser, String clickHousePass) throws Exception {
        connect(ip, username, password);
        //String command = "clickhouse-client --query=\"SHOW DATABASES\"";
        String command = String.format(
                "clickhouse-client --user=%s --password=%s --query=\"SHOW DATABASES\"",
                clickHouseUser, clickHousePass
        );
        CommandResult commandResult = executeCommand(command);
        if (!commandResult.isSuccess()) {
            throw new RuntimeException("Failed to get list db: \n" + commandResult.getMessage());
        }
        List<String> dbs = new ArrayList<>();
        for (String line : commandResult.getMessage().split("\n")) {
            if (!line.trim().isEmpty()) {
                dbs.add(line.trim());
            }
        }
        return dbs;
    }

    @Override
    public List<String> getListTableOfDB(String ip, String username, String password,
                                         String clickHouseUser, String clickHousePass, String dbName) throws Exception {
        connect(ip, username, password);
        //String command = String.format("clickhouse-client --query=\\\"SHOW TABLES FROM %s\\\"", dbName);
        String command = String.format(
                "clickhouse-client --user=%s --password=%s --query=\"SHOW TABLES FROM %s\"",
                clickHouseUser, clickHousePass, dbName
        );
        CommandResult commandResult = executeCommand(command);
        if (!commandResult.isSuccess()) {
            throw new RuntimeException("Failed to get tables from DB: " + dbName + "\n" + commandResult.getMessage());
        }
        List<String> tables = new ArrayList<>();
        for (String line : commandResult.getMessage().split("\n")) {
            if (!line.trim().isEmpty()) {
                tables.add(line.trim());
            }
        }
        return tables;
    }

    @Override
    public String authClickHouse(String clickHouseUser, String clickHousePass) {
        String auth = "";
        if (clickHouseUser != null && !clickHouseUser.isBlank()) {
            auth = String.format("--user=%s", clickHouseUser);
            if (clickHousePass != null && !clickHousePass.isBlank()) {
                auth += String.format(" --password=%s", clickHousePass);
            }
        }
        return auth;
    }

    @Override
    public boolean checkExistsFile(String fileName, String folderPath) throws Exception{
        String fullPath;
        if (folderPath.endsWith(fileName)) {
            // folderPath đã là fullPath
            fullPath = folderPath;
        } else {
            // folderPath là thư mục, cần nối thêm fileName
            fullPath = folderPath.endsWith("/") ? folderPath + fileName : folderPath + "/" + fileName;
        }
        String command = String.format("if [ -f %s ]; then echo 'EXISTS'; else echo 'NOT_EXISTS'; fi", fullPath);
        CommandResult commandResult = executeCommand(command);
        return commandResult.getMessage().contains("EXISTS"); //tồn tại -> return true
    }
}
