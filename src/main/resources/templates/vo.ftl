package com.portfolio.dbmetadatagenerator.generated;

import lombok.Getter;
import lombok.Setter;
<#if columns?filter(c -> c.javaType == "LocalDateTime")?size gt 0>
    import java.time.LocalDateTime;
</#if>
<#if columns?filter(c -> c.javaType == "BigDecimal")?size gt 0>
    import java.math.BigDecimal;
</#if>

/**
* ${tableRemarks!""}
*/
@Getter
@Setter
public class ${className} {

<#list columns as column>
    /**
    * ${column.remarks!""}
    */
    private ${column.javaType} ${column.fieldName};

</#list>
}