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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class SelectByExampleWithoutBLOBsElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectByExampleWithoutBLOBsElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        String fqjt = introspectedTable.getExampleType();

        XmlElement answer = new XmlElement("select");

        answer.addAttribute(new Attribute("id",
                introspectedTable.getSelectByExampleStatementId()));
        answer.addAttribute(new Attribute(
                "resultMap", introspectedTable.getBaseResultMapId()));
        answer.addAttribute(new Attribute("parameterType", "map"));

        context.getCommentGenerator().addComment(answer);

        answer.addElement(new TextElement("select"));
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "example != null and example.distinct"));
        ifElement.addElement(new TextElement("distinct"));
        answer.addElement(ifElement);

        StringBuilder sb = new StringBuilder();
        if (stringHasValue(introspectedTable
                .getSelectByExampleQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByExampleQueryId());
            sb.append("' as QUERYID,");
            answer.addElement(new TextElement(sb.toString()));
        }
        answer.addElement(getBaseColumnListElement());

        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        // example 封装到map中需要用update的
        answer.addElement(getUpdateByExampleIncludeElement());

        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "example != null and example.orderByClause != null"));
        ifElement.addElement(new TextElement("order by ${example.orderByClause}"));
        answer.addElement(ifElement);

        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "offSet != null and pageSize != null"));
        ifElement.addElement(new TextElement(" limit ${offSet},${pageSize} "));
        answer.addElement(ifElement);

        if (context.getPlugins()
                .sqlMapSelectByExampleWithoutBLOBsElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
