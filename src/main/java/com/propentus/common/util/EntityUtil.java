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

package com.propentus.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * Contains various useful methods for entity manipulation
 *
 */
public class EntityUtil {

	private static final Logger logger = LoggerFactory.getLogger(EntityUtil.class);

	/**
	 * Transforms class object's fields to key value HashMap. Useful, for
	 * example when we need to save Grails domain class to session.
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static HashMap<String, String> entityToHash(Object entity, String... fieldNames)
		throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
		SecurityException {
		HashMap<String, String> entityHash = new HashMap<String, String>();

		for (String fieldName : fieldNames) {
			// Get field value and add to hash if not null
			Object fieldVal = getFieldValueByName(entity, fieldName);
			if (fieldVal != null) {
				entityHash.put(fieldName, fieldVal.toString());
			}
		}
		return entityHash;
	}

	/**
	 * Transforms collection of objects's fields to key value HashMap. Useful,
	 * for example when we need to save Grails domains class to session.
	 * 
	 * @param entities
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static <T> List<HashMap<String, String>> entitiesToHash(Collection<T> entities,
		String... fieldNames) throws IllegalArgumentException, IllegalAccessException,
		NoSuchFieldException, SecurityException {
		List<HashMap<String, String>> entityHashList = new ArrayList<HashMap<String, String>>();

		for (Object entity : entities) {
			HashMap<String, String> entityHash = new HashMap<String, String>();

			for (String fieldName : fieldNames) {
				// Get field value and add to hash if not null
				Object fieldVal = getFieldValueByName(entity, fieldName);
				if (fieldVal != null) {
					entityHash.put(fieldName, fieldVal.toString());
				}
			}

			entityHashList.add(entityHash);
		}
		return entityHashList;
	}

	/**
	 * Returns specific field's value from object
	 * 
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static Object getFieldValueByName(Object entity, String fieldName)
		throws SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = null;
		try {
			field = entity.getClass().getDeclaredField(fieldName);
		} catch (Exception e) {

			logger
				.warn(
					"Entity of class: '{0}' didn't contain field: '{1}'. If you are sure class contains it, treat this message as false positive because of Grails bugging with domain classes.",
					entity.getClass().getName(), fieldName);
			return null;
		}
		field.setAccessible(true);
		Object fieldVal = field.get(entity);
		return fieldVal;
	}

	/**
	 * Converts Grails domain object collection to transfer object list
	 * 
	 * @param t Class of transfer object
	 * @param domainList Collection of domain objects
	 * @return List of transfer objects
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> List<T> domainListToTransferObjectList(Class<T> t, Collection<?> domainList)
		throws InstantiationException, IllegalAccessException {

		List<T> transferObjectList = new ArrayList<T>();

		for (Object domainObj : domainList) {
			T transfer = domainToTransferObject(t, domainObj);
			transferObjectList.add(transfer);
		}
		return transferObjectList;
	}

	/**
	 * Converts Grails domain object to transfer object
	 * 
	 * @param t Class of transfer object
	 * @param domainObj Domain object
	 * @return Transfer object
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T domainToTransferObject(Class<T> t, Object domainObj)
		throws InstantiationException, IllegalAccessException {
		T transferObject = t.newInstance();
		copyFields(transferObject, domainObj);
		return transferObject;
	}
	
	/***
	 * Convert XML String to JAXB annotated Object presentation
	 * @param t
	 * @param xml
	 * @return
	 * @throws JAXBException
	 */
	public static <T> T XMLtoObject(Class<T> t, String xml) throws JAXBException {
		//Read xml string to reader
		StringReader reader = new StringReader(xml);
		
		JAXBContext context = JAXBContext.newInstance(t);
	    Unmarshaller unMarshaller = context.createUnmarshaller();
	    return (T) unMarshaller.unmarshal(reader);
	}
	
	/***
	 * Convert JAXB annotated Object to XML String. XML String is formatted while marshalling.
	 * @param obj Object to be marshalled to XML
	 * @return Formatted XML representation of Object
	 * @throws JAXBException
	 */
	public static <T> String ObjectToXML(Object obj) throws JAXBException {
		StringWriter sw = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(obj.getClass());
	    Marshaller marshaller = context.createMarshaller();
	    //Formats XML to more readable form
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    marshaller.marshal(obj, sw);
	    return sw.toString();
	}

	public static String ObjectToJson(Object o) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(o);
	}

	public static <T> T JsonToObject(String json, Class<T> type) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(json, type);
	}

	/**
	 * Copy field values from object to another if field names are identical.
	 * 
	 * @param target Object, where field values are copied to
	 * @param source Object, where field values are copied from
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static void copyFields(Object target, Object source) throws IllegalArgumentException,
		IllegalAccessException {

		Field[] sourceFields = source.getClass().getDeclaredFields();
		Field[] targetFields = target.getClass().getDeclaredFields();
		for (Field sourceField : sourceFields) {
			sourceField.setAccessible(true);
			if (sourceField.getName().equals("serialVersionUID")) {
				continue;
			}
			if (Modifier.isFinal(sourceField.getModifiers())) {
				continue;
			}
			for (Field targetField : targetFields) {
				targetField.setAccessible(true);
				String source_field_name = sourceField.getName().toLowerCase();
				String target_field_name = targetField.getName().toLowerCase();

				if (source_field_name.equals(target_field_name)) {
					Object targetFieldType = targetField.getType();
					Object sourceFieldType = sourceField.getType();
					if (targetFieldType.equals(sourceFieldType)) {
						Object sourceValue = sourceField.get(source);
						targetField.set(target, sourceValue);
					} else if (sourceField.getType().isPrimitive()) {
						Object sourceValue = convertPrimitiveTypeValue(sourceField,
							sourceField.get(source));
						if (sourceValue != null) {
							targetField.set(target, sourceValue);
						}
					}
					break;
				}
			}
		}
	}

	private static Object convertPrimitiveTypeValue(Field field, Object value)
		throws IllegalArgumentException, IllegalAccessException {
		if (field.getType().getName().equals("int")) {
			return Integer.class.cast(value);
		} else if (field.getType().getName().equals("double")) {
			return Double.class.cast(value);
		} else if (field.getType().getName().equals("short")) {
			return Short.class.cast(value);
		}
		return null;
	}
	
	public static String serialize(Object obj) {
		String encoded = null;
		  
		  try {
		   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		   ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		   objectOutputStream.writeObject(obj);
		   objectOutputStream.close();
		   encoded = new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()));
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		  return encoded;
	}
	
	public static <T extends Serializable> T deserialize(Class<T> t, String objStr) {
		
		 byte[] bytes = Base64.getDecoder().decode(objStr.getBytes());
		  T object = null;
		  try {
		   ObjectInputStream objectInputStream = new ObjectInputStream( new ByteArrayInputStream(bytes) );
		   object = (T)objectInputStream.readObject();
		  } catch (IOException e) {
		   e.printStackTrace();
		  } catch (ClassNotFoundException e) {
		   e.printStackTrace();
		  } catch (ClassCastException e) {
		   e.printStackTrace();
		  }
		  return object;
	}
}
