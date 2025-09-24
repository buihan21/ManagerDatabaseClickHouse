package com.clickhouse.managerDB.payload.request;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
public class ComFindReq {
    private Integer id;
    private String ipAddress;
    private String name;
    private String location;
    private final Integer fetchCount = 1000;

    public Pageable getPageable() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return PageRequest.of(0, getFetchCount(), sort);
    }
}
