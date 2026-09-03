package com.portfolio.dbmetadatagenerator.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TableMetadata {

    private String tableName;              // 테이블명
    private String remarks;                // 테이블 코멘트(설명)
    private List<ColumnMetadata> columns;  // 이 테이블이 가진 컬럼들
}