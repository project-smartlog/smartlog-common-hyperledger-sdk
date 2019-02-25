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

package com.propentus.common.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationUtil {

	public static boolean methodHasAnnotation(Method method,
		Class<? extends Annotation> annotationClass) {
		return method.getAnnotation(annotationClass) != null;
	}
	
	public static boolean methodParentHasAnnotation(Method method,
			Class<? extends Annotation> annotationClass) {
			return method.getClass().getAnnotation(annotationClass) != null;
		}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean ClazzExtendsClazz(Class base, Class sub) {
		return base.isAssignableFrom(sub);
	}

	public static boolean fieldHasAnnotation(Field field,
		Class<? extends Annotation> annotationClass) {
		return field.isAnnotationPresent(annotationClass);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T getAnnotation(Field field,
			Class<? extends Annotation> annotationClass) {
			
		return (T) field.getDeclaredAnnotation(annotationClass);
	}
	
	/**
	 * Find annotation object for class, method or field, if present.
	 * @param annotatedElem
	 * @param annotationClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElem,
			Class<? extends Annotation> annotationClass) {
			
		return (T) annotatedElem.getDeclaredAnnotation(annotationClass);
	}

}
