package com.clickhouse.managerDB.payload.request;

import com.clickhouse.managerDB.constant.JobStatus;
import com.clickhouse.managerDB.constant.JobType;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class JobReq {
    private Long id;
    private JobStatus status;
    private JobType type;
    private String ipServer;
    private Long from;
    private Long to;
    private final Integer fetchCount = 1000;

    public Pageable getPageable() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return PageRequest.of(0, getFetchCount(), sort);
    }

    public LocalDateTime getFromDate() {
        return from == null ? null :
                Instant.ofEpochMilli(from)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
    }
    public LocalDateTime getToDate() {
        return to == null ? null :
                Instant.ofEpochMilli(to)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
    }
}
