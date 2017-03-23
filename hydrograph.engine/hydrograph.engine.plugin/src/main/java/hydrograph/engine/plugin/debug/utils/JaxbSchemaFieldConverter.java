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
package hydrograph.engine.plugin.debug.utils;

import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.jaxb.commontypes.*;

import javax.xml.namespace.QName;
import java.util.Set;
/**
 * The Class JaxbSchemaFieldConverter.
 *
 * @author Bitwise
 *
 */
public class JaxbSchemaFieldConverter {

	private JaxbSchemaFieldConverter() {
	}

	/**
	 * Converts the {@link SchemaField} objects to jaxb object {@link TypeOutputInSocket}
	 * @param schemaFieldList the set of {@link SchemaField} objects to convert to jaxb object {@link TypeOutputInSocket}
	 * @return jaxb object {@link TypeOutputInSocket}
	 */
	public static TypeOutputInSocket convertToJaxb(Set<SchemaField> schemaFieldList) {
		TypeOutputInSocket typeOutputInSocket = new TypeOutputInSocket();
		TypeBaseRecord record = new TypeBaseRecord();
		for (SchemaField schemaField : schemaFieldList) {
			TypeBaseField typeBaseField = new TypeBaseField();
			typeBaseField.setName(schemaField.getFieldName());
			setFieldScale(schemaField, typeBaseField);
			setFieldPrecision(schemaField, typeBaseField);
			// setFieldScaleType(schemaField, typeBaseField);
			setFieldFormat(schemaField, typeBaseField);
			typeBaseField.setType(FieldDataTypes.fromValue(schemaField.getFieldDataType()));
			// typeBaseField.setType(FieldDataTypes.JAVA_LANG_STRING);
			record.getFieldOrRecordOrIncludeExternalSchema().add(typeBaseField);
		}
		typeOutputInSocket.setSchema(record);
		return typeOutputInSocket;
	}

	/**
	 * Sets the field scale in the jaxb object of {@link TypeBaseField} from {@link SchemaField}
	 * @param schemaField the {@link SchemaField} object containing the field scale information
	 * @param typeBaseField he jaxb object to set the field scale to
	 */
	public static void setFieldScale(SchemaField schemaField, TypeBaseField typeBaseField) {
		typeBaseField.setScale(schemaField.getFieldScale());
	}

	/**
	 * Sets the field precision in the jaxb object of {@link TypeBaseField} from {@link SchemaField}
	 * @param schemaField the {@link SchemaField} object containing the field precision information
	 * @param typeBaseField he jaxb object to set the field precision to
	 */
	public static void setFieldPrecision(SchemaField schemaField, TypeBaseField typeBaseField) {
		typeBaseField.setPrecision(schemaField.getFieldPrecision());
	}

	/**
	 * Sets the field scale type in the jaxb object of {@link TypeBaseField} from {@link SchemaField}
	 * @param schemaField the {@link SchemaField} object containing the field scale type information
	 * @param typeBaseField he jaxb object to set the field scale type to
	 */
	public static void setFieldScaleType(SchemaField schemaField, TypeBaseField typeBaseField) {
		if (schemaField.getFieldScaleType().equals(ScaleTypeList.EXPLICIT))
			typeBaseField.setScaleType(ScaleTypeList.EXPLICIT);
		else
			typeBaseField.setScaleType(ScaleTypeList.IMPLICIT);
	}

	/**
	 * Sets the field format in the jaxb object of {@link TypeBaseField} from {@link SchemaField}
	 * @param schemaField the {@link SchemaField} object containing the field format information
	 * @param typeBaseField he jaxb object to set the field format to
	 */
	public static void setFieldFormat(SchemaField schemaField, TypeBaseField typeBaseField) {
		if (schemaField.getFieldFormat() != null) {
			if (schemaField.getFieldDataType().toLowerCase().contains("date")) {
				typeBaseField.setFormat("yyyy-MM-dd HH:mm:ss");
			} else {
				typeBaseField.setFormat(schemaField.getFieldFormat());
			}
		}
	}

	/**
	 * Sets the field length in the jaxb object of {@link TypeBaseField} from {@link SchemaField}
	 * @param schemaField the {@link SchemaField} object containing the field length information
	 * @param typeBaseField he jaxb object to set the field length to
	 */
	public static void setFieldLength(SchemaField schemaField, TypeBaseField typeBaseField) {
		QName qname = new QName("length");
		if (schemaField.getFieldLength() != 0)
			typeBaseField.getOtherAttributes().put(qname, String.valueOf(schemaField.getFieldLength()));
		else
			typeBaseField.getOtherAttributes().put(qname, String.valueOf(0));
	}
}