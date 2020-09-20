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

import org.mybatis.generator.api.dom.kotlin.KotlinArg;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.kotlin.KotlinFunction;

public class GeneralUpdateMethodGenerator extends AbstractKotlinFunctionGenerator {
    private String mapperName;
    
    private GeneralUpdateMethodGenerator(Builder builder) {
        super(builder);
        this.mapperName = builder.mapperName;
    }

    @Override
    public KotlinFunctionAndImports generateMethodAndImports() {
        KotlinFunctionAndImports functionAndImports = KotlinFunctionAndImports.withFunction(
                KotlinFunction.newOneLineFunction(mapperName + ".update")
                .withArgument(KotlinArg.newArg("completer")
                        .withDataType("UpdateCompleter")
                        .build())
                .withCodeLine("update(this::update, " + tableFieldName + ", completer)")
                .build())
                .withImport("org.mybatis.dynamic.sql.util.kotlin.*")
                .withImport("org.mybatis.dynamic.sql.util.kotlin.mybatis3.*")
                .build();

        addFunctionComment(functionAndImports);
        return functionAndImports;
    }

    @Override
    public boolean callPlugins(KotlinFunction kotlinFunction, KotlinFile kotlinFile) {
        return context.getPlugins().clientGeneralUpdateMethodGenerated(kotlinFunction, kotlinFile, introspectedTable);
    }

    public static class Builder extends BaseBuilder<Builder, GeneralUpdateMethodGenerator> {
        private String mapperName;

        public Builder withMapperName(String mapperName) {
            this.mapperName = mapperName;
            return this;
        }
        
        @Override
        public Builder getThis() {
            return this;
        }

        @Override
        public GeneralUpdateMethodGenerator build() {
            return new GeneralUpdateMethodGenerator(this);
        }
    }
}
