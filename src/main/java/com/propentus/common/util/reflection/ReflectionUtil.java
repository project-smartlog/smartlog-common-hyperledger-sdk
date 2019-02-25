/*
 *
 *  * Copyright 2016-2019
 *  *
 *  * Interreg Central Baltic 2014-2020 funded project
 *  * Smart Logistics and Freight Villages Initiative, CB426
 *  *
 *  * Kouvola Innovation Oy, FINLAND
 *  * Region Örebro County, SWEDEN
 *  * Tallinn University of Technology, ESTONIA
 *  * Foundation Valga County Development Agency, ESTONIA
 *  * Transport and Telecommunication Institute, LATVIA
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.propentus.common.util.reflection;

import com.propentus.common.util.DataFormatter;
import com.propentus.common.util.annotation.AllowNull;
import com.propentus.common.util.annotation.AnnotationUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This utility class contains various helper methods related to reflection.
 */

public class ReflectionUtil {

    /**
     * Try to find null field values from object. If all values are set, return empty arraylist
     * @return Name of fields, which are null or empty list if all field contain value.
     */
    public static List<String> findNullValues(Object obj) throws IllegalAccessException {

        List<String> nullValues = new ArrayList<String>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for(Field field : declaredFields) {

            field.setAccessible(true);
            //Field value was null
            if (field.get(obj) == null) {
                nullValues.add(field.getName());
            }
        }

        return nullValues;
    }

    /**
     * Try to find null field values from object. If all values are set, return empty arraylist
     * @return Name of fields, which are null or empty list if all field contain value. If field is annotated with @AllowNull, skip null check.
     */
    public static List<String> findNullValuesWithAnnotations(Object obj) throws IllegalAccessException {

        List<String> nullValues = new ArrayList<String>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for(Field field : declaredFields) {

            //Null is allowed, skip check
            if(AnnotationUtil.fieldHasAnnotation(field, AllowNull.class)) {
                continue;
            }

            field.setAccessible(true);
            //Field value was null
            if (field.get(obj) == null) {
                nullValues.add(field.getName());
            }
        }

        return nullValues;
    }

    public static String printFieldValues(Object obj) throws IllegalAccessException {

        StringBuilder sb = new StringBuilder();
        final String template = "{0}: '{1}' \n";

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for(Field field : declaredFields) {

            field.setAccessible(true);

            Object value = field.get(obj);

            if (value != null) {
                String logLine = DataFormatter.fillTemplate(template, field.getName(), String.valueOf(value));
                sb.append(logLine);
            }
            else {
                String logLine = DataFormatter.fillTemplate(template, field.getName(), "null");
                sb.append(logLine);
            }

        }

        return sb.toString();
    }

}
