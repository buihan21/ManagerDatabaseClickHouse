package com.clickhouse.managerDB.service;

import com.clickhouse.managerDB.dto.ComputerDTO;

import java.util.List;

public interface ComputerService {
    List<ComputerDTO> getAllComputers() throws Exception;
    ComputerDTO getComputerByIp(String ipAddress) throws Exception;

}
