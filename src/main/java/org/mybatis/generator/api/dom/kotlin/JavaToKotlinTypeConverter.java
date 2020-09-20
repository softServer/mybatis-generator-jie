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
package org.mybatis.generator.api.dom.kotlin;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * This class converts Java types to Kotlin types. It is not meant to cover all cases.
 * The main purpose is to convert type calculated by the database introspector into appropriate
 * types for Kotlin - which means that it covers Java primitives, Strings, and primitive wrapper
 * classes as those Java types have replacements in Kotlin.
 *  
 * @author Jeff Butler
 *
 */
public class JavaToKotlinTypeConverter {
    private JavaToKotlinTypeConverter() {}
    
    private static Map<String, String> typeMap = new HashMap<>();
    
    static {
        // string
        typeMap.put("java.lang.String", "kotlin.String");
        typeMap.put("java.lang.String[]", "kotlin.Array<kotlin.String>");

        // primitives
        typeMap.put("byte", "kotlin.Byte");
        typeMap.put("char", "kotlin.Char");
        typeMap.put("short", "kotlin.Short");
        typeMap.put("int", "kotlin.Int");
        typeMap.put("long", "kotlin.Long");
        typeMap.put("float", "kotlin.Float");
        typeMap.put("double", "kotlin.Double");
        typeMap.put("boolean", "kotlin.Boolean");

        // primitive arrays
        typeMap.put("byte[]", "kotlin.ByteArray");
        typeMap.put("char[]", "kotlin.CharArray");
        typeMap.put("short[]", "kotlin.ShortArray");
        typeMap.put("int[]", "kotlin.IntArray");
        typeMap.put("long[]", "kotlin.LongArray");
        typeMap.put("float[]", "kotlin.FloatArray");
        typeMap.put("double[]", "kotlin.DoubleArray");
        typeMap.put("boolean[]", "kotlin.BooleanArray");

        // primitive wrappers
        typeMap.put("java.lang.Byte", "kotlin.Byte");
        typeMap.put("java.lang.Character", "kotlin.Char");
        typeMap.put("java.lang.Short", "kotlin.Short");
        typeMap.put("java.lang.Integer", "kotlin.Int");
        typeMap.put("java.lang.Long", "kotlin.Long");
        typeMap.put("java.lang.Float", "kotlin.Float");
        typeMap.put("java.lang.Double", "kotlin.Double");
        typeMap.put("java.lang.Boolean", "kotlin.Boolean");

        // primitive wrapper arrays
        typeMap.put("java.lang.Byte[]", "kotlin.Array<kotlin.Byte>");
        typeMap.put("java.lang.Character[]", "kotlin.Array<kotlin.Char>");
        typeMap.put("java.lang.Short[]", "kotlin.Array<kotlin.Short>");
        typeMap.put("java.lang.Integer[]", "kotlin.Array<kotlin.Int>");
        typeMap.put("java.lang.Long[]", "kotlin.Array<kotlin.Long>");
        typeMap.put("java.lang.Float[]", "kotlin.Array<kotlin.Float.");
        typeMap.put("java.lang.Double[]", "kotlin.Array<kotlin.Double>");
        typeMap.put("java.lang.Boolean[]", "kotlin.Array<kotlin.Boolean>");
    }

    public static FullyQualifiedKotlinType convert(FullyQualifiedJavaType javaType) {
        FullyQualifiedKotlinType kotlinType = convertBaseType(javaType);
        
        for (FullyQualifiedJavaType argument : javaType.getTypeArguments()) {
            kotlinType.addTypeArgument(convert(argument));
        }
        
        return kotlinType;
    }
    
    private static FullyQualifiedKotlinType convertBaseType(FullyQualifiedJavaType javaType) {
        String typeName = javaType.getFullyQualifiedNameWithoutTypeParameters();
        return new FullyQualifiedKotlinType(typeMap.getOrDefault(typeName, typeName));
    }
}
