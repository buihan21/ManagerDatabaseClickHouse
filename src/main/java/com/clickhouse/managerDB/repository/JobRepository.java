package com.clickhouse.managerDB.repository;

import com.clickhouse.managerDB.constant.JobStatus;
import com.clickhouse.managerDB.constant.JobType;
import com.clickhouse.managerDB.entity.Job;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long>, JpaSpecificationExecutor<Job> {
    default Page<Job> findBy(Long id, JobStatus status, JobType jobType, String ipServer,
                             LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Specification<Job> spec = (Root<Job> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (jobType != null) {
                predicates.add(criteriaBuilder.equal(root.get("jobType"), jobType));
            }
            if (ipServer != null) {
                predicates.add(criteriaBuilder.equal(root.get("ipServer"), ipServer));
            }
            if (from != null && to != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get("createdAt"), from, to)));
            } else if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from));
            } else if (to != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
//        if (pageable == null || pageable.isUnpaged()) {
//            return this.findAll(spec);
//        }
        Pageable safePageable = (pageable == null ? Pageable.unpaged() : pageable);
        return this.findAll(spec, safePageable);
    }
}
