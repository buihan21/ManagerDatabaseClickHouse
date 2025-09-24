package com.clickhouse.managerDB.repository;

import com.clickhouse.managerDB.entity.Computer;
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

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ComputerRepository extends JpaRepository<Computer,Integer>, JpaSpecificationExecutor<Computer> {
    Computer findByIpAddress(String ipAddress);
    default Page<Computer> findBy(Integer id, String ipAddress, String name, String location, Pageable pageable) {
        Specification<Computer> spec = (Root<Computer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            if (name != null) {
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }
            if (location != null) {
                predicates.add(criteriaBuilder.equal(root.get("location"), location));
            }
            if (ipAddress != null) {
                predicates.add(criteriaBuilder.equal(root.get("ipAddress"), ipAddress));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Pageable safePageable = (pageable == null ? Pageable.unpaged() : pageable);
        return findAll(spec, safePageable);
    }

    Computer findComputerById(Integer id);
}
