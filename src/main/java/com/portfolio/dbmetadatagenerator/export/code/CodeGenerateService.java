package com.portfolio.dbmetadatagenerator.export.code;

import com.portfolio.dbmetadatagenerator.metadata.ColumnMetadata;
import com.portfolio.dbmetadatagenerator.metadata.TableMetadata;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodeGenerateService {

    private final Configuration freemarkerConfig;

    public CodeGenerateService() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_34);
        this.freemarkerConfig.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(), "templates");
    }

    public String generateVo(TableMetadata tableMetadata) throws IOException, TemplateException {
        // 1) 템플릿에 넘겨줄 데이터 준비
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("className", toPascalCase(tableMetadata.getTableName()) + "VO");
        dataModel.put("tableRemarks", tableMetadata.getRemarks());

        List<GeneratedColumn> generatedColumns = new ArrayList<>();
        for (ColumnMetadata column : tableMetadata.getColumns()) {
            generatedColumns.add(new GeneratedColumn(
                    mapToJavaType(column.getDataType()),
                    toCamelCase(column.getColumnName()),
                    column.getRemarks()
            ));
        }
        dataModel.put("columns", generatedColumns);

        // 2) 템플릿 로드 + 데이터 합쳐서 문자열로 뽑아내기
        Template template = freemarkerConfig.getTemplate("vo.ftl");
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);

        return writer.toString();
    }

    // 스네이크케이스 → 카멜케이스 (member_id → memberId)
    String toCamelCase(String snakeCase) {
        StringBuilder result = new StringBuilder();
        boolean upperNext = false;
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else {
                result.append(upperNext ? Character.toUpperCase(c) : c);
                upperNext = false;
            }
        }
        return result.toString();
    }

    // 스네이크케이스 → 파스칼케이스 (members → Members)
    String toPascalCase(String snakeCase) {
        String camel = toCamelCase(snakeCase);
        return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }

    // DB 타입명 → Java 타입명 매핑
    String mapToJavaType(String dbType) {
        String type = dbType.toLowerCase();
        if (type.contains("bigserial") || type.contains("bigint")) return "Long";
        if (type.contains("int")) return "Integer";
        if (type.contains("varchar") || type.contains("text")) return "String";
        if (type.contains("bool")) return "Boolean";
        if (type.contains("timestamp") || type.contains("date")) return "LocalDateTime";
        if (type.contains("decimal") || type.contains("numeric")) return "BigDecimal";
        return "Object"; // 매핑 안 되는 타입은 일단 Object로 (나중에 보완 가능)
    }
}