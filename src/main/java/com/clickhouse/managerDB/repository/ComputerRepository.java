package com.clickhouse.managerDB.repository;

import com.clickhouse.managerDB.entity.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComputerRepository extends JpaRepository<Computer,Integer> {
    Computer findByIpAddress(String ipAddress);
}
