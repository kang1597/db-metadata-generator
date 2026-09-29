package com.portfolio.dbmetadatagenerator.metadata;

import com.portfolio.dbmetadatagenerator.connection.DbConnection;
import com.portfolio.dbmetadatagenerator.connection.DbConnectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private DbConnection savePostgresConnection() {
        DbConnection dbConnection = new DbConnection();
        dbConnection.setName("테스트 PostgreSQL");
        dbConnection.setDbType("POSTGRESQL");
        dbConnection.setHost("localhost");
        dbConnection.setPort(5432);
        dbConnection.setDatabaseName("sampledb");
        dbConnection.setUsername("dev");
        dbConnection.setPassword("dev1234");
        return dbConnectionRepository.save(dbConnection);
    }

    private DbConnection saveMariaDbConnection() {
        DbConnection dbConnection = new DbConnection();
        dbConnection.setName("테스트 MariaDB");
        dbConnection.setDbType("MARIADB");
        dbConnection.setHost("localhost");
        dbConnection.setPort(3307);
        dbConnection.setDatabaseName("sampledb");
        dbConnection.setUsername("dev");
        dbConnection.setPassword("dev1234");
        return dbConnectionRepository.save(dbConnection);
    }

    @Nested
    @DisplayName("PostgreSQL")
    class PostgresTest {

        @Test
        @DisplayName("테이블 목록 조회 시 사용자 테이블이 포함되고 시스템 테이블은 제외된다")
        void getTableNames_시스템테이블제외() throws SQLException {
            // given
            DbConnection saved = savePostgresConnection();

            // when
            List<String> tableNames = metadataService.getTableNames(saved.getId());

            // then
            assertThat(tableNames)
                    .contains("members", "posts")
                    .noneMatch(name -> name.startsWith("pg_"))
                    .doesNotContain("sql_features", "sql_parts");
        }

        @Test
        @DisplayName("테이블 상세 조회 시 PK 컬럼이 정확히 식별된다")
        void getTableDetail_PK컬럼확인() throws SQLException {
            // given
            DbConnection saved = savePostgresConnection();

            // when
            TableMetadata result = metadataService.getTableDetail(saved.getId(), "members");

            // then
            assertThat(result.getColumns())
                    .filteredOn(ColumnMetadata::isPrimaryKey)
                    .extracting(ColumnMetadata::getColumnName)
                    .containsExactly("member_id");
        }

        @Test
        @DisplayName("테이블 코멘트가 정확히 조회된다")
        void getTableDetail_코멘트확인() throws SQLException {
            // given
            DbConnection saved = savePostgresConnection();

            // when
            TableMetadata result = metadataService.getTableDetail(saved.getId(), "members");

            // then
            assertThat(result.getRemarks()).isEqualTo("회원 정보");
        }
    }

    @Nested
    @DisplayName("MariaDB")
    class MariaDbTest {

        @Test
        @DisplayName("테이블 목록 조회 시 접속한 DB의 테이블만 반환한다 (시스템 DB 제외)")
        void getTableNames_접속DB로범위제한() throws SQLException {
            // given
            DbConnection saved = saveMariaDbConnection();

            // when
            List<String> tableNames = metadataService.getTableNames(saved.getId());

            // then
            // catalog를 지정하지 않으면 information_schema, performance_schema의
            // 테이블(global_status 등)까지 조회되므로, 정확히 2개만 나오는지 검증
            assertThat(tableNames).containsExactlyInAnyOrder("members", "posts");
        }

        @Test
        @DisplayName("테이블 상세 조회 시 PK 컬럼이 정확히 식별된다")
        void getTableDetail_PK컬럼확인() throws SQLException {
            // given
            DbConnection saved = saveMariaDbConnection();

            // when
            TableMetadata result = metadataService.getTableDetail(saved.getId(), "members");

            // then
            assertThat(result.getColumns())
                    .filteredOn(ColumnMetadata::isPrimaryKey)
                    .extracting(ColumnMetadata::getColumnName)
                    .containsExactly("member_id");
        }

        @Test
        @DisplayName("PostgreSQL과 타입명 표기가 달라도 동일한 컬럼 정보를 반환한다")
        void getTableDetail_타입명대문자표기() throws SQLException {
            // given
            DbConnection saved = saveMariaDbConnection();

            // when
            TableMetadata result = metadataService.getTableDetail(saved.getId(), "members");

            // then
            assertThat(result.getRemarks()).isEqualTo("회원 정보");
            assertThat(result.getColumns())
                    .extracting(ColumnMetadata::getColumnName)
                    .containsExactly("member_id", "email", "nickname",
                            "password", "created_at", "is_active");
        }
    }
}