package com.portfolio.dbmetadatagenerator.export.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneratedColumn {
    private String javaType;    // Java 타입 (예: Long, String)
    private String fieldName;   // 카멜케이스 필드명 (예: memberId)
    private String remarks;     // 설명
}