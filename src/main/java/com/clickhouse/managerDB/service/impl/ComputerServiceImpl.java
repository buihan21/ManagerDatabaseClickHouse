package com.clickhouse.managerDB.service.impl;

import com.clickhouse.managerDB.dto.ComputerDTO;
import com.clickhouse.managerDB.entity.Computer;
import com.clickhouse.managerDB.repository.ComputerRepository;
import com.clickhouse.managerDB.service.ComputerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComputerServiceImpl implements ComputerService {
    @Autowired
    private ComputerRepository computerRepository;
    @Override
    public List<ComputerDTO> getAllComputers() throws Exception {
        List<Computer>  computers = computerRepository.findAll();
        List<ComputerDTO> result = new ArrayList<>();
        for (Computer computer : computers) {
            result.add(ComputerDTO.from(computer));
        }
        return result;
    }

    @Override
    public ComputerDTO getComputerByIp(String ipAddress) throws Exception {
        Computer computer = computerRepository.findByIpAddress(ipAddress);
        return ComputerDTO.from(computer);
    }
}
