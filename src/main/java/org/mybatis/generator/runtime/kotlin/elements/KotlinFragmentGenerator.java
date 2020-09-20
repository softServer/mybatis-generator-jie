/**
 *    Copyright 2006-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.runtime.kotlin.elements;

import static org.mybatis.generator.api.dom.OutputUtilities.kotlinIndent;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForKotlin;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.kotlin.FullyQualifiedKotlinType;
import org.mybatis.generator.api.dom.kotlin.JavaToKotlinTypeConverter;
import org.mybatis.generator.api.dom.kotlin.KotlinArg;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.runtime.kotlin.KotlinDynamicSqlSupportClassGenerator;

public class KotlinFragmentGenerator {

    private IntrospectedTable introspectedTable;
    private String resultMapId;
    private String tableFieldImport;
    
    private KotlinFragmentGenerator(Builder builder) {
        this.introspectedTable = builder.introspectedTable;
        this.resultMapId = builder.resultMapId;
        tableFieldImport = builder.tableFieldImport;
    }
    
    public KotlinFunctionParts getPrimaryKeyWhereClauseAndParameters() {
        KotlinFunctionParts.Builder builder = new KotlinFunctionParts.Builder();
        
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            FullyQualifiedKotlinType kt = JavaToKotlinTypeConverter.convert(column.getFullyQualifiedJavaType());
            
            String fieldName = column.getJavaProperty();
            String argName = column.getJavaProperty() + "_";
            
            builder.withImport(tableFieldImport + "." + fieldName);
            builder.withImports(kt.getImportList());
            builder.withArgument(KotlinArg.newArg(argName)
                    .withDataType(kt.getShortNameWithTypeArguments())
                    .build());
            if (first) {
                builder.withCodeLine("    where(" + fieldName
                        + ", isEqualTo(" + argName
                        + "))");
                first = false;
            } else {
                builder.withCodeLine("    and(" + fieldName
                        + ", isEqualTo(" + argName
                        + "))");
            }
        }
        builder.withCodeLine("}");
        
        return builder.build();
    }

    public KotlinFunctionParts getPrimaryKeyWhereClauseForUpdate() {
        KotlinFunctionParts.Builder builder = new KotlinFunctionParts.Builder();
        
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            String fieldName = column.getJavaProperty();
            
            builder.withImport(tableFieldImport + "." + fieldName);
            if (first) {
                builder.withCodeLine("    where(" + fieldName
                        + ", isEqualTo(record::" + fieldName
                        + "))");
                first = false;
            } else {
                builder.withCodeLine("    and(" + fieldName
                        + ", isEqualTo(record::" + fieldName
                        + "))");
            }
        }
        builder.withCodeLine("}");
        
        return builder.build();
    }

    public KotlinFunctionParts getAnnotatedResults() {
        KotlinFunctionParts.Builder builder = new KotlinFunctionParts.Builder();

        builder.withImport("org.apache.ibatis.type.JdbcType");
        builder.withImport("org.apache.ibatis.annotations.Result");
        builder.withImport("org.apache.ibatis.annotations.Results");

        builder.withAnnotation("@Results(id=\"" + resultMapId + "\", value = [");

        StringBuilder sb = new StringBuilder();

        Set<String> imports = new HashSet<>();
        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);
            kotlinIndent(sb, 1);
            sb.append(getResultAnnotation(imports, introspectedColumn, true));
            
            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);
            kotlinIndent(sb, 1);
            sb.append(getResultAnnotation(imports, introspectedColumn, false));
            
            if (iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        builder.withAnnotation("])")
            .withImports(imports);
        
        return builder.build();
    }
    
    private String getResultAnnotation(Set<String> imports, IntrospectedColumn introspectedColumn,
            boolean idColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("Result(column=\"");
        sb.append(escapeStringForKotlin(introspectedColumn.getActualColumnName()));
        sb.append("\", property=\"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append('\"');

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            FullyQualifiedKotlinType fqjt =
                    new FullyQualifiedKotlinType(introspectedColumn.getTypeHandler());
            imports.add(introspectedColumn.getTypeHandler());
            sb.append(", typeHandler=");
            sb.append(fqjt.getShortNameWithoutTypeArguments());
            sb.append("::class");
        }

        sb.append(", jdbcType=JdbcType.");
        sb.append(introspectedColumn.getJdbcTypeName());
        if (idColumn) {
            sb.append(", id=true");
        }
        sb.append(')');

        return sb.toString();
    }
    
    public KotlinFunctionParts getGeneratedKeyAnnotation(GeneratedKey gk) {
        KotlinFunctionParts.Builder builder = new KotlinFunctionParts.Builder();
        
        StringBuilder sb = new StringBuilder();
        introspectedTable.getColumn(gk.getColumn()).ifPresent(introspectedColumn -> {
            if (gk.isJdbcStandard()) {
                builder.withImport("org.apache.ibatis.annotations.Options");
                sb.append("@Options(useGeneratedKeys=true,keyProperty=\"record.");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append("\")");
                builder.withAnnotation(sb.toString());
            } else {
                builder.withImport("org.apache.ibatis.annotations.SelectKey");
                FullyQualifiedKotlinType kt =
                        JavaToKotlinTypeConverter.convert(introspectedColumn.getFullyQualifiedJavaType());
                
                sb.append("@SelectKey(statement=[\"");
                sb.append(gk.getRuntimeSqlStatement());
                sb.append("\"], keyProperty=\"record.");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append("\", before=");
                sb.append(gk.isIdentity() ? "false" : "true");
                sb.append(", resultType=");
                sb.append(kt.getShortNameWithoutTypeArguments());
                sb.append("::class)");
                builder.withAnnotation(sb.toString());
            }
        });
        
        return builder.build();
    }

    public KotlinFunctionParts getSetEqualLines(List<IntrospectedColumn> columnList) {
        
        KotlinFunctionParts.Builder builder = new KotlinFunctionParts.Builder();
        
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(columnList);
        for (IntrospectedColumn column : columns) {
            String fieldName = column.getJavaProperty();
            builder.withImport(tableFieldImport + "." + fieldName);
            
            builder.withCodeLine("    set(" + fieldName
                    + ").equalTo(record::" + column.getJavaProperty()
                    + ")");
        }

        return builder.build();
    }
    
    public KotlinFunctionParts getSetEqualWhenPresentLines(List<IntrospectedColumn> columnList) {
        
        KotlinFunctionParts.Builder builder = new KotlinFunctionParts.Builder();
        
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(columnList);
        for (IntrospectedColumn column : columns) {
            String fieldName = column.getJavaProperty();
            builder.withImport(tableFieldImport + "." + fieldName);
            
            builder.withCodeLine("    set(" + fieldName
                    + ").equalToWhenPresent(record::" + column.getJavaProperty()
                    + ")");
        }

        return builder.build();
    }

    public static class Builder {
        private IntrospectedTable introspectedTable;
        private String resultMapId;
        private String tableFieldImport;
        
        public Builder withIntrospectedTable(IntrospectedTable introspectedTable) {
            this.introspectedTable = introspectedTable;
            return this;
        }
        
        public Builder withResultMapId(String resultMapId) {
            this.resultMapId = resultMapId;
            return this;
        }
        
        public Builder withDynamicSqlSupportClassGenerator(KotlinDynamicSqlSupportClassGenerator generator) {
            tableFieldImport = generator.getInnerObjectImport();
            return this;
        }
        
        public KotlinFragmentGenerator build() {
            return new KotlinFragmentGenerator(this);
        }
    }
}
