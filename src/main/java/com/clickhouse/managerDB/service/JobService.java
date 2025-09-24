package com.clickhouse.managerDB.service;

import com.clickhouse.managerDB.constant.JobStatus;
import com.clickhouse.managerDB.constant.JobType;
import com.clickhouse.managerDB.dto.JobDTO;
import com.clickhouse.managerDB.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface JobService {
    Job createJob(JobType type, String ipServer, String target);
    void updateStatus(Long jobId, JobStatus status, String errorMessage);
    Job getJob(Long jobId);
    void submitJob(Long jobId, Runnable task);
    List<Job> findJobs(Long id, JobStatus status, JobType type, String ipServer,
                       LocalDateTime from, LocalDateTime to, Pageable pageable);
}
