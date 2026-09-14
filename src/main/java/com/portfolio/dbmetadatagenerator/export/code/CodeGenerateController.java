package com.portfolio.dbmetadatagenerator.export.code;

import com.portfolio.dbmetadatagenerator.metadata.MetadataService;
import com.portfolio.dbmetadatagenerator.metadata.TableMetadata;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/export/code")
@RequiredArgsConstructor
public class CodeGenerateController {

    private final MetadataService metadataService;
    private final CodeGenerateService codeGenerateService;

    @GetMapping(value = "/{connectionId}/tables/{tableName}/vo",
                produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateVO(
            @PathVariable Long connectionId,
            @PathVariable String tableName) throws SQLException, IOException, TemplateException {

        TableMetadata tableMetadata = metadataService.getTableDetail(connectionId, tableName);
        return codeGenerateService.generateVo(tableMetadata);
    }
}
