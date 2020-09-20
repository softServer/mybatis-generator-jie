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
package org.mybatis.generator.codegen.mybatis3.model;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansField;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansGetter;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansSetter;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.RootClassInfo;

public class BaseRecordGenerator extends AbstractJavaGenerator {

    public BaseRecordGenerator(String project) {
        super(project);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.8", table.toString()));
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);
        // 所有的自动对象 actualColumnName 数据库列名 javaProperty java字段名
        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass, introspectedTable.getNonBLOBColumns());

            if (includeBLOBColumns()) {
                addParameterizedConstructor(topLevelClass, introspectedTable.getAllColumns());
            }

            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }

        // 静态常量添加
        Field allColumnListField = getAllColmnListField();
        topLevelClass.addField(allColumnListField);
        topLevelClass.addImportedType(allColumnListField.getType());
        StringBuilder allColumnListInitializationString = new StringBuilder();
        allColumnListInitializationString.append("new ArrayList<String>() {\n" )
                .append("\t\tprivate static final long serialVersionUID = 1L;\n")
                .append("\t\t{\n");
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            Field columnField = getColumnField(introspectedColumn);
            topLevelClass.addField(columnField);
            topLevelClass.addImportedType(allColumnListField.getType());
            allColumnListInitializationString.append("\t\t\tadd(\"")
                    .append(introspectedColumn.getActualColumnName())
                    .append("\");\n");
        }
        allColumnListInitializationString.append("\t\t}\t}");
        allColumnListField.setInitializationString(allColumnListInitializationString.toString());

        String rootClass = getRootClass();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings)
                    .containsProperty(introspectedColumn)) {
                continue;
            }

            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }

            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
            }

            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
                        introspectedColumn, introspectedTable,
                        Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }
            }
        }

        List<CompilationUnit> answer = new ArrayList<>();
        if (context.getPlugins().modelBaseRecordClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    private Field getColumnField(IntrospectedColumn introspectedColumn) {
        String property = introspectedColumn.getJavaProperty();

        Field field = new Field("S_".concat(property), FullyQualifiedJavaType.getStringInstance());
        field.setFinal(true);
        field.setStatic(true);
        field.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder initializationString = new StringBuilder();
        initializationString.append("\"")
                .append(introspectedColumn.getActualColumnName())
                .append("\"");
        field.setInitializationString(initializationString.toString());

        return field;
    }

    private Field getAllColmnListField() {

        FullyQualifiedJavaType paramListType = FullyQualifiedJavaType.getNewArrayListInstance();
        FullyQualifiedJavaType stringType = FullyQualifiedJavaType.getStringInstance();
        paramListType.addTypeArgument(stringType);

        Field field = new Field("fieldList", paramListType);
        field.setFinal(true);
        field.setStatic(true);
        field.setVisibility(JavaVisibility.PUBLIC);
        return field;
    }

    private FullyQualifiedJavaType getSuperClass() {
        FullyQualifiedJavaType superClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable
                    .getPrimaryKeyType());
        } else {
            String rootClass = getRootClass();
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }

        return superClass;
    }

    private boolean includePrimaryKeyColumns() {
        return !introspectedTable.getRules().generatePrimaryKeyClass()
                && introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !introspectedTable.getRules().generateRecordWithBLOBsClass()
                && introspectedTable.hasBLOBColumns();
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass, List<IntrospectedColumn> constructorColumns) {
        Method method = new Method(topLevelClass.getType().getShortName());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                    introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }

        StringBuilder sb = new StringBuilder();
        List<String> superColumns = new LinkedList<>();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            boolean comma = false;
            sb.append("super(");
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                if (comma) {
                    sb.append(", ");
                } else {
                    comma = true;
                }
                sb.append(introspectedColumn.getJavaProperty());
                superColumns.add(introspectedColumn.getActualColumnName());
            }
            sb.append(");");
            method.addBodyLine(sb.toString());
        }

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            if (!superColumns.contains(introspectedColumn.getActualColumnName())) {
                sb.setLength(0);
                sb.append("this.");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" = ");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(';');
                method.addBodyLine(sb.toString());
            }
        }

        topLevelClass.addMethod(method);
    }

    private List<IntrospectedColumn> getColumnsInThisClass() {
        List<IntrospectedColumn> introspectedColumns;
        if (includePrimaryKeyColumns()) {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getAllColumns();
            } else {
                introspectedColumns = introspectedTable.getNonBLOBColumns();
            }
        } else {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable
                        .getNonPrimaryKeyColumns();
            } else {
                introspectedColumns = introspectedTable.getBaseColumns();
            }
        }

        return introspectedColumns;
    }
}
