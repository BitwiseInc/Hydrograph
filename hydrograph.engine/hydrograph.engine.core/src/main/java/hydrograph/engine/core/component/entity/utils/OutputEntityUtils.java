/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.core.component.entity.utils;

import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.core.constants.Constants;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The Class OutputEntityUtils.
 *
 * @author Bitwise
 *
 */
public class OutputEntityUtils implements Serializable{

	private OutputEntityUtils() {
	}

	/**
	 * Extracts the field type for all the fields from the Schema of output
	 * socket object of type {@link TypeBaseField}, passed as a parameter
	 * 
	 * @param # TypeBaseField
	 *            list of {@link TypeBaseField} objects which contain the fields
	 *            type information
	 * @return a list of {@link SchemaField} objects. Each object in the list
	 *         contains information of Fields class, fieldName, fieldType,
	 *         fieldsFormat, fieldScale for one field
	 * 
	 * @throws NullPointerException
	 *             when {@code outSocketList} is null
	 */
	public static List<SchemaField> extractOutputFields(List<Object> list) {

		if (list == null)
			throw new NullPointerException("Outsocket Schema cannot be null");
		List<SchemaField> fieldList = new ArrayList<SchemaField>();

		for (int i = 0; i < list.size(); i++) {
			SchemaField fields = new SchemaField(((TypeBaseField) list.get(i)).getName(),
					((TypeBaseField) list.get(i)).getType().value());

			setFieldLength(list, fields, i);

			setFieldFormat(list, fields, i);

			setFieldScale(list, fields, i);

			setFieldScaleType(list, fields, i);

			setFieldPrecision(list, fields, i);
			
			setFieldLengthDelimeter(list,fields,i);
			
			setTypeofLengthDelimeter(list,fields,i);
			
			setColDef(list,fields,i);

			setAbsoluteOrRelativeXPath(list, fields, i);
			
			fieldList.add(fields);
		}

		return fieldList;

	}

	/**
	 * Sets the AbsoluteOrRelativeXPath value for the SchemaField object
	 *
	 * @param list
	 *            of {@link TypeBaseField} objects which contain the fields type
	 *            information
	 * @param fields
	 *            of {@link SchemaField} objects which contain the fields type
	 *            information
	 * @param i
	 *            index
	 */
	private static void setAbsoluteOrRelativeXPath(List<Object> list, SchemaField fields, int i) {
		if (((TypeBaseField) list.get(i)).getOtherAttributes() != null) {
			Map<QName, String> otherAttributes = ((TypeBaseField) list.get(i))
					.getOtherAttributes();
			for (QName absoluteOrRelativeXPath : otherAttributes.keySet()) {
				if (absoluteOrRelativeXPath.getLocalPart().equalsIgnoreCase(
						"absoluteOrRelativeXPath"))
					fields.setAbsoluteOrRelativeXPath(otherAttributes.get(absoluteOrRelativeXPath));
			}
		}
	}

	private static void setTypeofLengthDelimeter(List<Object> list,
			SchemaField fields, int i) {
		if (((TypeBaseField) list.get(i)).getOtherAttributes() != null) {
			Map<QName, String> lengthDelimiter =((TypeBaseField) list.get(i)).getOtherAttributes(); 
			for ( QName fieldLengthDelimiter : lengthDelimiter.keySet()) {
				
				if(fieldLengthDelimiter.getLocalPart().equalsIgnoreCase("length")){
					fields.setTypeFieldLengthDelimiter(Integer.class);	
				}else if(fieldLengthDelimiter.getLocalPart().equalsIgnoreCase("delimiter")){
					fields.setTypeFieldLengthDelimiter(String.class);
				}
			}
		}
	}

	private static void setFieldLengthDelimeter(List<Object> list,
			SchemaField fields, int i) {
		if (((TypeBaseField) list.get(i)).getOtherAttributes() != null) {
			Map<QName, String> lengthDelimiter =((TypeBaseField) list.get(i)).getOtherAttributes(); 
			for ( QName fieldLengthDelimiter : lengthDelimiter.keySet()) {
				if(fieldLengthDelimiter.getLocalPart().equalsIgnoreCase("length") || 
						fieldLengthDelimiter.getLocalPart().equalsIgnoreCase("delimiter"))
				fields.setFieldLengthDelimiter(lengthDelimiter.get(fieldLengthDelimiter));
			}
		}
	}

	/**
	 * @param list
	 *            of {@link TypeBaseField} objects which contain the fields type
	 *            information
	 * @param fields
	 *            of {@link SchemaField} objects which contain the fields type
	 *            information
	 * @param i
	 *            index
	 */
	private static void setFieldScaleType(List<Object> list, SchemaField fields, int i) {
		if (((TypeBaseField) list.get(i)).getScaleType() != null) {
			fields.setFieldScaleType(((TypeBaseField) list.get(i)).getScaleType().value());
		}
	}
	
	/**
	 * @param list
	 *            of {@link TypeBaseField} objects which contain the fields type
	 *            information
	 * @param fields
	 *            of {@link SchemaField} objects which contain the fields type
	 *            information
	 * @param i
	 *            index
	 */
	private static void setFieldPrecision(List<Object> list, SchemaField fields, int i) {
		if (((TypeBaseField) list.get(i)).getPrecision() != null) {
			fields.setFieldPrecision(((TypeBaseField) list.get(i)).getPrecision());
		}
		else{
			fields.setFieldPrecision(Constants.DEFAULT_PRECISION);
		}
	}

	/**
	 * @param list
	 *            of {@link TypeBaseField} objects which contain the fields type
	 *            information
	 * @param fields
	 *            of {@link SchemaField} objects which contain the fields type
	 *            information
	 * @param i
	 *            index
	 */
	private static void setFieldScale(List<Object> list, SchemaField fields, int i) {
		if (((TypeBaseField) list.get(i)).getScale() != null) {
			fields.setFieldScale(((TypeBaseField) list.get(i)).getScale());
		}
		else{
			fields.setFieldScale(Constants.DEFAULT_SCALE);
		}
	}

	/**
	 * @param list
	 *            of {@link TypeBaseField} objects which contain the fields type
	 *            information
	 * @param fields
	 *            of {@link SchemaField} objects which contain the fields type
	 *            information
	 * @param i
	 *            index
	 */
	private static void setFieldFormat(List<Object> list, SchemaField fields, int i) {
		if (((TypeBaseField) list.get(i)).getFormat() != null) {
			fields.setFieldFormat(((TypeBaseField) list.get(i)).getFormat());
		}
	}

	/**
	 * @param list
	 *            of {@link TypeBaseField} objects which contain the fields type
	 *            information
	 * @param fields
	 *            of {@link SchemaField} objects which contain the fields type
	 *            information
	 * @param i
	 *            index
	 */
	private static void setFieldLength(List<Object> list, SchemaField fields, int i) {
		Map<QName, String> hashmap = ((TypeBaseField) list.get(i)).getOtherAttributes();
		if (hashmap.containsKey(new QName("length")))
			fields.setFieldLength(Integer.parseInt(hashmap.get(new QName("length"))));
	}

	
	/**
	 * @param list
	 *            of {@link TypeBaseField} objects which contain the fields type
	 *            information
	 * @param fields
	 *            of {@link SchemaField} objects which contain the fields type
	 *            information
	 * @param i
	 *            index
	 */
	private static void setColDef(List<Object> list, SchemaField fields, int i) {
		Map<QName, String> hashmap = ((TypeBaseField) list.get(i)).getOtherAttributes();
		if (hashmap.containsKey(new QName("colDef")))
			fields.setColDef((hashmap.get(new QName("colDef"))));
	}
	
	
	/**
	 * Extracts the {@link Properties} object from the {@link TypeProperties}
	 * object passed as a parameter
	 * <p>
	 * The method returns {@code null} if the {@code typeProperties} parameter
	 * is null
	 * @param typeProperties
	 *            the {@link TypeProperties} object which contain information of
	 *            runtime properties for the component
	 * @return a {@link Properties} object
	 */
	public static Properties extractRuntimeProperties(TypeProperties typeProperties) {
		
		if (typeProperties == null) {
			return null;
		} else if (typeProperties.getProperty() == null) {
			return null;
		}
		Properties properties = new Properties();
		// Fetch all the properties passed to operation
		for (Property eachProperty : typeProperties.getProperty()) {
			properties.setProperty(eachProperty.getName(), eachProperty.getValue());
		}

		return properties;
	}
}
