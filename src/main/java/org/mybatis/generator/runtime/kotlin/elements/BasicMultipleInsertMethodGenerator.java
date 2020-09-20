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

import org.mybatis.generator.api.dom.kotlin.FullyQualifiedKotlinType;
import org.mybatis.generator.api.dom.kotlin.KotlinArg;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.kotlin.KotlinFunction;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.runtime.dynamic.sql.elements.v2.Utils;

public class BasicMultipleInsertMethodGenerator extends AbstractKotlinFunctionGenerator {
    
    private FullyQualifiedKotlinType recordType;
    
    private BasicMultipleInsertMethodGenerator(Builder builder) {
        super(builder);
        recordType = builder.recordType;
    }

    @Override
    public KotlinFunctionAndImports generateMethodAndImports() {
        if (!Utils.generateMultipleRowInsert(introspectedTable)) {
            return null;
        }
        
        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk == null) {
            return generateMethodWithoutGeneratedKeys();
        } else {
            return generateMethodWithGeneratedKeys(gk);
        }
    }
    
    private KotlinFunctionAndImports generateMethodWithoutGeneratedKeys() {
        
        KotlinFunctionAndImports functionAndImports = KotlinFunctionAndImports.withFunction(
                KotlinFunction.newOneLineFunction("insertMultiple")
                .withExplicitReturnType("Int")
                .withArgument(KotlinArg.newArg("multipleInsertStatement")
                        .withDataType("MultiRowInsertStatementProvider<"
                                + recordType.getShortNameWithTypeArguments()
                                + ">")
                        .build())
                .withAnnotation("@InsertProvider(type=SqlProviderAdapter::class,"
                        + " method=\"insertMultiple\")")
                .build())
                .withImport("org.mybatis.dynamic.sql.util.SqlProviderAdapter")
                .withImport("org.apache.ibatis.annotations.InsertProvider")
                .withImport("org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider")
                .withImports(recordType.getImportList())
                .build();
        
        addFunctionComment(functionAndImports);

        return functionAndImports;
    }

    private KotlinFunctionAndImports generateMethodWithGeneratedKeys(GeneratedKey gk) {
        
        KotlinFunctionAndImports functionAndImports = KotlinFunctionAndImports.withFunction(
                KotlinFunction.newOneLineFunction("insertMultiple")
                .withExplicitReturnType("Int")
                .withAnnotation("@Insert(")
                .withAnnotation("    \"\\${insertStatement}\"")
                .withAnnotation(")")
                .withArgument(KotlinArg.newArg("insertStatement")
                        .withAnnotation("@Param(\"insertStatement\")")
                        .withDataType("String")
                        .build())
                .withArgument(KotlinArg.newArg("records")
                        .withAnnotation("@Param(\"records\")")
                        .withDataType("List<"
                                + recordType.getShortNameWithTypeArguments()
                                + ">")
                        .build())
                .build())
                .withImport("org.apache.ibatis.annotations.Insert")
                .withImport("org.apache.ibatis.annotations.Param")
                .withImports(recordType.getImportList())
                .build();
                
        addFunctionComment(functionAndImports);
                
                
        KotlinFunctionParts functionParts = getGeneratedKeyAnnotation(gk);
        acceptParts(functionAndImports, functionParts);
        
        return functionAndImports;
    }

    private KotlinFunctionParts getGeneratedKeyAnnotation(GeneratedKey gk) {
        KotlinFunctionParts.Builder builder = new KotlinFunctionParts.Builder();
        
        StringBuilder sb = new StringBuilder();
        introspectedTable.getColumn(gk.getColumn()).ifPresent(introspectedColumn -> {
            if (gk.isJdbcStandard()) {
                // only jdbc standard keys are supported for multiple insert
                builder.withImport("org.apache.ibatis.annotations.Options");
                sb.append("@Options(useGeneratedKeys=true,keyProperty=\"records.");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append("\")");
                builder.withAnnotation(sb.toString());
            }
        });
        
        return builder.build();
    }

    @Override
    public boolean callPlugins(KotlinFunction kotlinFunction, KotlinFile kotlinFile) {
        return context.getPlugins().clientBasicInsertMultipleMethodGenerated(kotlinFunction, kotlinFile,
                introspectedTable);
    }

    public static class Builder extends BaseBuilder<Builder, BasicMultipleInsertMethodGenerator> {

        private FullyQualifiedKotlinType recordType;
        
        public Builder withRecordType(FullyQualifiedKotlinType recordType) {
            this.recordType = recordType;
            return this;
        }
        
        @Override
        public Builder getThis() {
            return this;
        }

        @Override
        public BasicMultipleInsertMethodGenerator build() {
            return new BasicMultipleInsertMethodGenerator(this);
        }
    }
}
