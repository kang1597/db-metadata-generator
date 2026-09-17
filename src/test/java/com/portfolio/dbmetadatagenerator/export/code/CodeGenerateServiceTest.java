package com.portfolio.dbmetadatagenerator.export.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeGenerateServiceTest {

    private final CodeGenerateService codeGenerateService = new CodeGenerateService();

    @Test
    @DisplayName("스네이크케이스를 카멜케이스로 변환한다")
    void toCamelCase_변환성공() {
        // given
        String snakeCase = "member_id";

        // when
        String result = codeGenerateService.toCamelCase(snakeCase);

        // then
        assertThat(result).isEqualTo("memberId");
    }

    @Test
    @DisplayName("여러 단어로 이루어진 스네이크케이스도 정확히 변환한다")
    void toCamelCase_여러단어_변환성공() {
        // given
        String snakeCase = "created_at";

        // when
        String result = codeGenerateService.toCamelCase(snakeCase);

        // then
        assertThat(result).isEqualTo("createdAt");
    }

    @Test
    @DisplayName("언더스코어가 없는 문자열은 그대로 반환한다")
    void toCamelCase_언더스코어없음_그대로반환() {
        // given
        String snakeCase = "email";

        // when
        String result = codeGenerateService.toCamelCase(snakeCase);

        // then
        assertThat(result).isEqualTo("email");
    }

    @Test
    @DisplayName("테이블명을 파스칼케이스로 변환한다")
    void toPascalCase_변환성공() {
        // given
        String snakeCase = "members";

        // when
        String result = codeGenerateService.toPascalCase(snakeCase);

        // then
        assertThat(result).isEqualTo("Members");
    }

    @Test
    @DisplayName("bigserial 타입은 Long으로 매핑한다")
    void mapToJavaType_bigserial은Long으로() {
        // when
        String result = codeGenerateService.mapToJavaType("bigserial");

        // then
        assertThat(result).isEqualTo("Long");
    }

    @Test
    @DisplayName("varchar 타입은 String으로 매핑한다")
    void mapToJavaType_varchar는String으로() {
        // when
        String result = codeGenerateService.mapToJavaType("varchar");

        // then
        assertThat(result).isEqualTo("String");
    }

    @Test
    @DisplayName("알 수 없는 타입은 Object로 매핑한다")
    void mapToJavaType_알수없는타입은Object로() {
        // when
        String result = codeGenerateService.mapToJavaType("어떤이상한타입");

        // then
        assertThat(result).isEqualTo("Object");
    }
}