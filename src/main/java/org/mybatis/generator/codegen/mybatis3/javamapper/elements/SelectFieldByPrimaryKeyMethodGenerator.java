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
package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * selectFieldByPrimaryKey
 */
public class SelectFieldByPrimaryKeyMethodGenerator extends
        AbstractJavaMapperMethodGenerator {

    private boolean isSimple;

    public SelectFieldByPrimaryKeyMethodGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Method method = new Method(introspectedTable.getSelectFieldByPrimaryKeyStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);

        FullyQualifiedJavaType returnType = introspectedTable.getRules()
                .calculateAllFieldsClass();
        method.setReturnType(returnType);

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        importedTypes.add(returnType);

        if (!isSimple && introspectedTable.getRules().generatePrimaryKeyClass()) {

            FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
            importedTypes.add(listType);
            FullyQualifiedJavaType stringType = FullyQualifiedJavaType.getStringInstance();
            importedTypes.add(stringType);
            listType.addTypeArgument(stringType);

            method.addParameter(new Parameter(listType, "filedList", "@Param(\"filedList\")"));

            FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                    introspectedTable.getPrimaryKeyType());
            importedTypes.add(type);
            method.addParameter(new Parameter(type, "key", "@Param(\"id\")"));

            importedTypes.add(new FullyQualifiedJavaType(
                    "org.apache.ibatis.annotations.Param"));
        } else {
            List<IntrospectedColumn> introspectedColumns = introspectedTable
                    .getPrimaryKeyColumns();
            boolean annotate = introspectedColumns.size() > 1;

            FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
            importedTypes.add(listType);
            FullyQualifiedJavaType stringType = FullyQualifiedJavaType.getStringInstance();
            importedTypes.add(stringType);
            listType.addTypeArgument(stringType);

            method.addParameter(new Parameter(listType, "filedList", "@Param(\"filedList\")"));

            importedTypes.add(new FullyQualifiedJavaType(
                    "org.apache.ibatis.annotations.Param"));

            StringBuilder sb = new StringBuilder();
            for (IntrospectedColumn introspectedColumn : introspectedColumns) {
                FullyQualifiedJavaType type = introspectedColumn
                        .getFullyQualifiedJavaType();
                importedTypes.add(type);
                Parameter parameter = new Parameter(type, introspectedColumn
                        .getJavaProperty());
                if (annotate) {
                    sb.setLength(0);
                    sb.append("@Param(\"");
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append("\")");
                    parameter.addAnnotation(sb.toString());
                }
                method.addParameter(parameter);
            }
        }

        addMapperAnnotations(interfaze, method);

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        if (context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(
                method, interfaze, introspectedTable)) {
            addExtraImports(interfaze);
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Interface interfaze, Method method) {
        // extension point for subclasses
    }

    public void addExtraImports(Interface interfaze) {
        // extension point for subclasses
    }
}
