package com.portfolio.dbmetadatagenerator.metadata;

import com.portfolio.dbmetadatagenerator.connection.DbConnection;
import com.portfolio.dbmetadatagenerator.connection.DbConnectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MetadataServiceTest {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private DbConnectionRepository dbConnectionRepository;

    private DbConnection saveTestConnection() {
        DbConnection dbConnection = new DbConnection();
        dbConnection.setName("테스트 연결");
        dbConnection.setDbType("POSTGRESQL");
        dbConnection.setHost("localhost");
        dbConnection.setPort(5432);
        dbConnection.setDatabaseName("sampledb");
        dbConnection.setUsername("dev");
        dbConnection.setPassword("dev1234");
        return dbConnectionRepository.save(dbConnection);
    }

    @Test
    @DisplayName("등록된 연결로 테이블 목록을 조회하면 members 테이블이 포함된다")
    void getTableNames_테이블목록조회성공() throws SQLException {
        // given
        DbConnection saved = saveTestConnection();

        // when
        List<String> tableNames = metadataService.getTableNames(saved.getId());

        // then
        assertThat(tableNames).contains("members", "posts");
    }

    @Test
    @DisplayName("members 테이블 상세 조회 시 PK 컬럼이 정확히 표시된다")
    void getTableDetail_PK컬럼확인() throws SQLException {
        // given
        DbConnection saved = saveTestConnection();

        // when
        TableMetadata result = metadataService.getTableDetail(saved.getId(), "members");

        // then
        assertThat(result.getColumns())
                .filteredOn(ColumnMetadata::isPrimaryKey)
                .extracting(ColumnMetadata::getColumnName)
                .containsExactly("member_id");
    }

    @Test
    @DisplayName("테이블 상세 조회 시 컬럼 코멘트가 정확히 조회된다")
    void getTableDetail_코멘트확인() throws SQLException {
        // given
        DbConnection saved = saveTestConnection();

        // when
        TableMetadata result = metadataService.getTableDetail(saved.getId(), "members");

        // then
        assertThat(result.getRemarks()).isEqualTo("회원 정보");
    }
}