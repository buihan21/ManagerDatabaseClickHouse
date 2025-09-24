package com.clickhouse.managerDB.controller;

import com.clickhouse.managerDB.payload.request.JobReq;
import com.clickhouse.managerDB.payload.response.ResponseObject;
import com.clickhouse.managerDB.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping("/")
    public ResponseEntity<ResponseObject> listJobs(@RequestBody JobReq req) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Find jobs")
                        .data(jobService.findJobs(req.getId(), req.getStatus(), req.getType(), req.getIpServer(),
                                req.getFromDate(), req.getToDate(), req.getPageable()))
                        .build()
        );
    }
}
