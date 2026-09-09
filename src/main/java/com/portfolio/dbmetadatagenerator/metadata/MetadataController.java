package com.portfolio.dbmetadatagenerator.metadata;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
public class MetadataController {

    private final MetadataService metadataService;

    // 특정 연결의 전체 테이블 목록 조회
    @GetMapping("/{connectionId}/tables")
    public List<String> getTableNames(@PathVariable Long connectionId) throws SQLException{
        return metadataService.getTableNames(connectionId);
    }

    // 특정 테이블의 상세 메타데이터 조회
    @GetMapping("/{connectionId}/tables/{tableName}")
    public TableMetadata getTableDetail(
            @PathVariable Long connectionId,
            @PathVariable String tableName) throws SQLException {
        return metadataService.getTableDetail(connectionId, tableName);
    }
}
