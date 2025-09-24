package com.clickhouse.managerDB.service.impl;

import com.clickhouse.managerDB.dto.ComputerDTO;
import com.clickhouse.managerDB.entity.Computer;
import com.clickhouse.managerDB.repository.ComputerRepository;
import com.clickhouse.managerDB.service.ComputerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Computer createComputer(String ipServer, Integer port, String name, String username, String password,
                                   String location, String clickHouseUser, String clickHousePass) throws Exception {
        Computer computer = new Computer();
        computer.setIpAddress(ipServer);
        computer.setPort(port);
        computer.setName(name);
        computer.setUsername(username);
        computer.setPassword(password);
        computer.setLocation(location);
        computer.setClickHouseUser(clickHouseUser);
        computer.setClickHousePass(clickHousePass);
        computer.setDeleted(false);
        return computerRepository.save(computer);
    }

    @Override
    public List<Computer> findComputer(Integer id, String ipServer, String name, String location, Pageable pageable) throws Exception {
        return computerRepository.findBy(id, ipServer, name, location, pageable).getContent();
    }

    @Override
    public Computer updateComputer(Integer id, String ipServer, Integer port, String name, String username, String password,
                                   String location, String clickHouseUser, String clickHousePass) throws Exception {
        Computer computer = computerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("computer not found with id: " + id));
        if (computer.isDeleted()) {
            throw new Exception("computer deleted");
        }
        computer.setIpAddress(ipServer);
        computer.setPort(port);
        computer.setName(name);
        computer.setUsername(username);
        computer.setPassword(password);
        computer.setLocation(location);
        computer.setClickHouseUser(clickHouseUser);
        computer.setClickHousePass(clickHousePass);
        return computerRepository.save(computer);
    }

    @Override
    public String deleteComputer(Integer id) throws Exception {
        Computer computer = computerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("computer not found with id: " + id));
        computer.setDeleted(true);
        computerRepository.save(computer);
        return "computer deleted";
    }


}
