package com.clickhouse.managerDB.service;

import com.clickhouse.managerDB.dto.ComputerDTO;
import com.clickhouse.managerDB.entity.Computer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ComputerService {
    List<ComputerDTO> getAllComputers() throws Exception;
    ComputerDTO getComputerByIp(String ipAddress) throws Exception;
    Computer createComputer(String ipServer, Integer port, String name, String username, String password,
                            String location, String clickHouseUser, String clickHousePass) throws Exception;
    List<Computer> findComputer(Integer id, String ipServer, String name, String location, Pageable pageable) throws Exception;
    Computer updateComputer(Integer id, String ipServer, Integer port, String name, String username, String password,
                            String location, String clickHouseUser, String clickHousePass) throws Exception;
    String deleteComputer(Integer id) throws Exception;
}
