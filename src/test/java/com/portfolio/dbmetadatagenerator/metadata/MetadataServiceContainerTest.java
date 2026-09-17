package com.portfolio.dbmetadatagenerator.metadata;

import com.portfolio.dbmetadatagenerator.connection.DbConnection;
import com.portfolio.dbmetadatagenerator.connection.DbConnectionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
@Disabled("""
        로컬 개발 환경(Docker Desktop Per-user 설치)의 Docker 소켓(npipe) 접근 제약으로 실행 불가.
        Docker 컨테이너 정상 기동 상태에서도 동일 에러가 재현되어 Docker 상태 문제가 아님을 확인함.
        현재는 docker-compose 기반 MetadataServiceTest로 통합 테스트를 대체하고 있으며,
        CI 환경 구성 시 본 테스트를 활성화할 예정.
        """)
class MetadataServiceContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private DbConnectionRepository dbConnectionRepository;

    @BeforeAll
    static void setUp() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE test_users (
                    user_id BIGSERIAL PRIMARY KEY,
                    username VARCHAR(50) NOT NULL
                )
            """);
        }
    }

    @Test
    @DisplayName("Testcontainers로 띄운 DB에서 테이블 목록을 조회한다")
    void getTableNames_컨테이너DB조회성공() throws SQLException {
        // given
        DbConnection dbConnection = new DbConnection();
        dbConnection.setName("컨테이너 테스트");
        dbConnection.setDbType("POSTGRESQL");
        dbConnection.setHost(postgres.getHost());
        dbConnection.setPort(postgres.getFirstMappedPort());
        dbConnection.setDatabaseName(postgres.getDatabaseName());
        dbConnection.setUsername(postgres.getUsername());
        dbConnection.setPassword(postgres.getPassword());
        DbConnection saved = dbConnectionRepository.save(dbConnection);

        // when
        List<String> tableNames = metadataService.getTableNames(saved.getId());

        // then
        assertThat(tableNames).contains("test_users");
    }
}