package com.portfolio.dbmetadatagenerator.export.excel;

import com.portfolio.dbmetadatagenerator.metadata.MetadataService;
import com.portfolio.dbmetadatagenerator.metadata.TableMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/export/excel")
@RequiredArgsConstructor
public class ExcelExportController {

    private final MetadataService metadataService;
    private final ExcelExportService excelExportService;

    @GetMapping("/{connectionId}/tables/{tableName}")
    public ResponseEntity<byte[]> exportExcel(
            @PathVariable Long connectionId,
            @PathVariable String tableName) throws SQLException, IOException {
        TableMetadata tableMetadata = metadataService.getTableDetail(connectionId, tableName);
        byte[] excelBytes = excelExportService.generateExcel(tableMetadata);

        String fileName = tableName + "_정의서.xlsx";
        String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodeFileName)
                .body(excelBytes);
    }
}
