package com.portfolio.dbmetadatagenerator.export.excel;

import com.portfolio.dbmetadatagenerator.metadata.ColumnMetadata;
import com.portfolio.dbmetadatagenerator.metadata.TableMetadata;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExcelExportService {

    public byte[] generateExcel(TableMetadata tableMetadata) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(tableMetadata.getTableName());

            // 헤더 스타일 만들기
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 1행: 테이블명/설명
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("테이블명");
            titleRow.createCell(1).setCellValue(tableMetadata.getTableName());
            Row descRow = sheet.createRow(1);
            descRow.createCell(0).setCellValue("설명");
            descRow.createCell(1).setCellValue(
                    tableMetadata.getRemarks() != null ? tableMetadata.getRemarks() : "");

            // 3행: 컬럼 헤더
            Row headerRow = sheet.createRow(3);
            String[] headers = {"컬럼명", "타입", "크기", "NULL 허용", "PK", "설명"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 4행부터: 컬럼 목록
            int rowIndex = 4;
            for (ColumnMetadata column : tableMetadata.getColumns()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(column.getColumnName());
                row.createCell(1).setCellValue(column.getDataType());
                row.createCell(2).setCellValue(column.getColumnSize());
                row.createCell(3).setCellValue(column.isNullable() ? "Y" : "N");
                row.createCell(4).setCellValue(column.isPrimaryKey() ? "Y" : "N");
                row.createCell(5).setCellValue(
                        column.getRemarks() != null ? column.getRemarks() : "");
            }

            // 컬럼 너비 자동 조정
            for (int i = 0; i < headers.length; i++){
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
