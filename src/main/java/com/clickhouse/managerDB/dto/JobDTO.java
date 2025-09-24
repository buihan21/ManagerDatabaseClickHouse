package com.clickhouse.managerDB.dto;

import com.clickhouse.managerDB.constant.JobStatus;
import com.clickhouse.managerDB.constant.JobType;
import com.clickhouse.managerDB.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {
    private JobType type;
    private String ipServer;
    private String target;
    private JobStatus status;
//    private String logFile;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
    public static Job toEntity(JobDTO dto){
        Job job = new Job();
        BeanUtils.copyProperties(dto,job);
        return job;
    }

    public static JobDTO toDTO(Job job){
        JobDTO dto = new JobDTO();
        BeanUtils.copyProperties(job,dto);
        return dto;
    }
}
