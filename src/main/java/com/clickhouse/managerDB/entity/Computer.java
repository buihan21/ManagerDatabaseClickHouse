package com.clickhouse.managerDB.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_computer")
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 100)
    private String name;
    @Column(nullable = false, length = 100)
    private String ipAddress;
    @Column(length = 10)
    private Integer port;
    @Column(nullable = false, length = 25)
    private String username;
    @Column(nullable = false, length = 25)
    private String password;
    private LocalDateTime createdAt = LocalDateTime.now();
}
