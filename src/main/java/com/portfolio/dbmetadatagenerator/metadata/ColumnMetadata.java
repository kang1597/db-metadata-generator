package com.portfolio.dbmetadatagenerator.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMetadata {

    private String columnName;      // 컬럼명
    private String dataType;        // 데이터 타입 (예: VARCHAR, INTEGER)
    private int columnSize;         // 길이 (예: VARCHAR(255)의 255)
    private boolean nullable;       // NULL 허용 여부
    private boolean primaryKey;     // PK 여부
    private String remarks;         // 컬럼 코멘트(설명)
}