package com.clickhouse.managerDB.service.impl;

import com.clickhouse.managerDB.config.MdcRunnable;
import com.clickhouse.managerDB.constant.JobStatus;
import com.clickhouse.managerDB.constant.JobType;
import com.clickhouse.managerDB.dto.JobDTO;
import com.clickhouse.managerDB.entity.Job;
import com.clickhouse.managerDB.repository.JobRepository;
import com.clickhouse.managerDB.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ExecutorService executor;

    @Override
    public Job createJob(JobType type, String ipServer, String target) {
        Job job = new Job();
        job.setType(type);
        job.setIpServer(ipServer);
        job.setTarget(target);
        job.setStatus(JobStatus.PENDING);
        //job.setLogFile(logFile);
        return jobRepository.save(job);
    }

    @Override
    public void updateStatus(Long jobId, JobStatus status, String errorMessage) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(status);
        job.setErrorMessage(errorMessage);
        jobRepository.save(job);
    }

    @Override
    public Job getJob(Long jobId) {
        return jobRepository.findById(jobId).orElseThrow();
    }

    @Override
    public void submitJob(Long jobId, Runnable task) {
        updateStatus(jobId, JobStatus.RUNNING, null);
        MDC.put("jobId", jobId.toString());
        executor.submit(new MdcRunnable(() -> {
            try {
                log.info("Start executing job {}", jobId);

                task.run();
                updateStatus(jobId, JobStatus.COMPLETED, null);

                log.info("Job {} completed", jobId);
            } catch (Exception ex) {
                updateStatus(jobId, JobStatus.FAILED,  ex.getMessage());
                log.error("Job {} failed: {}", jobId, ex.getMessage(), ex);
            }
        }));
        MDC.clear();
    }

    @Override
    public List<Job> findJobs(Long id, JobStatus status, JobType type, String ipServer,
                              LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return jobRepository.findBy(id, status, type, ipServer, from, to, pageable).getContent();
    }
}

