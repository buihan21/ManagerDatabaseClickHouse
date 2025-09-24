package com.clickhouse.managerDB.entity;

import com.clickhouse.managerDB.constant.JobStatus;
import com.clickhouse.managerDB.constant.JobType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "tbl_job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private JobType type; //backup, restore

    private String ipServer; // ip may remote

    private String target; // db name, table name

    @Enumerated(EnumType.STRING)
    private JobStatus status; // pending

    private String errorMessage;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
