package com.portfolio.dbmetadatagenerator.connection;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "db_connection")
@Getter
@Setter
@NoArgsConstructor
public class DbConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;              // 연결 별칭 (예: "회사 PostgreSQL")

    @Column(nullable = false, length = 20)
    private String dbType;            // "POSTGRESQL" or "MARIADB"

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer port;

    @Column(nullable = false, length = 100)
    private String databaseName;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;          // 추후 암호화 예정

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}