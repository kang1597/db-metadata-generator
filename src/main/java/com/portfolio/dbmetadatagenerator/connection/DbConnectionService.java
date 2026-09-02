package com.portfolio.dbmetadatagenerator.connection;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DbConnectionService {

    private final DbConnectionRepository repository;

    // 연결 정보 등록
    public DbConnection register(DbConnection dbConnection) {
        return repository.save(dbConnection);
    }

    // 전체 목록 조회
    public List<DbConnection> findAll() {
        return repository.findAll();
    }

    // 단건 조회
    public DbConnection findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("연결 정보를 찾을 수 없습니다. id=" + id));
    }

    // 실제 DB에 연결이 되는지 테스트
    public boolean testConnection(DbConnection dbConnection) {
        String url = buildJdbcUrl(dbConnection);

        try (Connection connection = DriverManager.getConnection(
                url, dbConnection.getUsername(), dbConnection.getPassword())) {
            return connection.isValid(3); // 3초 타임아웃
        } catch (SQLException e) {
            return false;
        }
    }

    // DB 타입에 따라 JDBC URL 형식을 다르게 조립
    private String buildJdbcUrl(DbConnection dbConnection) {
        String type = dbConnection.getDbType();
        String host = dbConnection.getHost();
        Integer port = dbConnection.getPort();
        String dbName = dbConnection.getDatabaseName();

        return switch (type) {
            case "POSTGRESQL" -> String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
            case "MARIADB" -> String.format("jdbc:mariadb://%s:%d/%s", host, port, dbName);
            default -> throw new IllegalArgumentException("지원하지 않는 DB 타입입니다: " + type);
        };
    }
}