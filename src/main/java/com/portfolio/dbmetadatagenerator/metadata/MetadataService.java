package com.portfolio.dbmetadatagenerator.metadata;

import com.portfolio.dbmetadatagenerator.connection.DbConnection;
import com.portfolio.dbmetadatagenerator.connection.DbConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MetadataService {

    private final DbConnectionRepository dbConnectionRepository;

    // 특정 연결의 전체 테이블 목록 조회 (테이블명만, 간단 버전)
    public List<String> getTableNames(Long connectionId) throws SQLException {
        DbConnection dbConnection = dbConnectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("연결 정보를 찾을 수 없습니다."));

        List<String> tableNames = new ArrayList<>();

        try (Connection conn = openConnection(dbConnection)) {
            DatabaseMetaData metaData = conn.getMetaData();

            // schema는 null로 두면 기본 스키마(현재 접속한 DB) 기준으로 조회됨
            try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }
        }

        return tableNames;
    }

    // 특정 테이블의 상세 메타데이터 조회 (컬럼, PK 포함)
    public TableMetadata getTableDetail(Long connectionId, String tableName) throws SQLException {
        DbConnection dbConnection = dbConnectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("연결 정보를 찾을 수 없습니다."));

        try (Connection conn = openConnection(dbConnection)) {
            DatabaseMetaData metaData = conn.getMetaData();

            // 1) PK 컬럼명들을 먼저 Set으로 모아둠 (나중에 컬럼 순회하며 대조하기 위함)
            Set<String> primaryKeys = new HashSet<>();
            try (ResultSet pkRs = metaData.getPrimaryKeys(null, null, tableName)) {
                while (pkRs.next()) {
                    primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                }
            }

            // 2) 테이블 코멘트 조회
            String tableRemarks = null;
            try (ResultSet tableRs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                if (tableRs.next()) {
                    tableRemarks = tableRs.getString("REMARKS");
                }
            }

            // 3) 컬럼 목록 조회
            List<ColumnMetadata> columns = new ArrayList<>();
            try (ResultSet colRs = metaData.getColumns(null, null, tableName, "%")) {
                while (colRs.next()) {
                    String columnName = colRs.getString("COLUMN_NAME");
                    columns.add(new ColumnMetadata(
                            columnName,
                            colRs.getString("TYPE_NAME"),
                            colRs.getInt("COLUMN_SIZE"),
                            "YES".equals(colRs.getString("IS_NULLABLE")),
                            primaryKeys.contains(columnName),
                            colRs.getString("REMARKS")
                    ));
                }
            }

            return new TableMetadata(tableName, tableRemarks, columns);
        }
    }

    // DbConnection 정보로 실제 JDBC 연결을 여는 헬퍼 메서드
    private Connection openConnection(DbConnection dbConnection) throws SQLException {
        String url = buildJdbcUrl(dbConnection);
        return DriverManager.getConnection(url, dbConnection.getUsername(), dbConnection.getPassword());
    }

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