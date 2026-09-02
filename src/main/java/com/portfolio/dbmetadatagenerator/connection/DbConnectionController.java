package com.portfolio.dbmetadatagenerator.connection;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class DbConnectionController {

    private final DbConnectionService dbConnectionService;

    // 연결 정보 등록
    @PostMapping
    public DbConnection register(@RequestBody DbConnection dbConnection) {
        return dbConnectionService.register(dbConnection);
    }

    // 전체 목록 조회
    @GetMapping
    public List<DbConnection> findAll() {
        return dbConnectionService.findAll();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public DbConnection findById(@PathVariable Long id) {
        return dbConnectionService.findById(id);
    }

    // 연결 테스트
    @PostMapping("/test")
    public boolean testConnection(@RequestBody DbConnection dbConnection) {
        return dbConnectionService.testConnection(dbConnection);
    }
}