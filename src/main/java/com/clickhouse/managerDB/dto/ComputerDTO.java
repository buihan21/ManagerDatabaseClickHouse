package com.clickhouse.managerDB.dto;

import com.clickhouse.managerDB.entity.Computer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComputerDTO {
    private Integer id;
    private String name;
    private String ipAddress;
    private Integer port;
    private String username;
    private String password;

    public static ComputerDTO from(Computer computer) {
        ComputerDTO dto = new ComputerDTO();
        BeanUtils.copyProperties(computer, dto);
        return dto;
    }
}
