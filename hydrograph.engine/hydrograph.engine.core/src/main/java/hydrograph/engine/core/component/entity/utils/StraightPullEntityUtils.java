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

import hydrograph.engine.core.component.entity.elements.KeyField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 * The Class StraightPullEntityUtils.
 *
 * @author Bitwise
 *
 */

public class StraightPullEntityUtils implements Serializable{

	private StraightPullEntityUtils() {
	}

	/**
	 * Extracts a list of all the sockets from the output socket object of type
	 * {@link TypeInputOutSocket}, passed as a parameter
	 * 
	 * @param # outSocket
	 *            list of {@link TypeInputOutSocket} objects which contain the
	 *            socket type information
	 * @return a list of {@link OutSocket} objects. Each object in the list
	 *         contains information of OutSocket class, socketId, socketType for
	 *         one operation
	 * 
	 * @throws NullPointerException
	 *             when {@code outSocketList} is null
	 */
	public static List<OutSocket> extractOutSocketList(
			List<TypeStraightPullOutSocket> list) {

		if (list == null)
			throw new NullPointerException("Out socket cannot be null");

		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		for (TypeStraightPullOutSocket aList : list) {
			OutSocket outSocket = new OutSocket(aList.getId());
			if (aList.getType() != null)
				outSocket.setSocketType(aList.getType());
			outSockets.add(outSocket);

		}
		return outSockets;
	}

	/**
	 * Extracts the key fields and sort order from the
	 * {@link hydrograph.engine.jaxb.removedups.TypeSecondaryKeyFields} object
	 * passed as a parameter
	 * <p>
	 * The method returns {@code null} if the {@code typeSecondaryKeyFields}
	 * parameter is null
	 * 
	 * @param typeSecondaryKeyFields
	 *            the object of
	 *            {@link hydrograph.engine.jaxb.removedups.TypeSecondaryKeyFields}
	 *            which contain information of secondary key fields for the
	 *            component
	 * @return a {@link KeyField} array containing the secondary key fields for
	 *         an operation
	 */
	public static KeyField[] extractSecondaryKeyFields(
			hydrograph.engine.jaxb.removedups.TypeSecondaryKeyFields typeSecondaryKeyFields) {
		if (typeSecondaryKeyFields == null
				|| typeSecondaryKeyFields.getField() == null) {
			return null;
		}
		KeyField[] keyFields = new KeyField[typeSecondaryKeyFields.getField()
				.size()];
		int i = 0;
		for (hydrograph.engine.jaxb.removedups.TypeSecondayKeyFieldsAttributes eachTypeFieldName : typeSecondaryKeyFields
				.getField()) {
			KeyField eachKeyField = new KeyField();
			eachKeyField.setName(eachTypeFieldName.getName());
			eachKeyField.setSortOrder(eachTypeFieldName.getOrder().value());
			keyFields[i] = eachKeyField;
			i++;
		}
		return keyFields;
	}

	/**
	 * Extracts the key fields and sort order from the
	 * {@link hydrograph.engine.jaxb.removedups.TypePrimaryKeyFields} object
	 * passed as a parameter
	 * <p>
	 * The method returns {@code null} if the {@code typePrimaryKeyFields}
	 * parameter is null
	 * 
	 * @param typePrimaryKeyFields
	 *            the object of
	 *            {@link hydrograph.engine.jaxb.removedups.TypePrimaryKeyFields}
	 *            which contain information of key fields for the component
	 * @return a {@link KeyField} array containing the key fields for an
	 *         operation
	 */
	public static KeyField[] extractKeyFields(
			hydrograph.engine.jaxb.removedups.TypePrimaryKeyFields typePrimaryKeyFields) {
		if (typePrimaryKeyFields == null) {
			return null;
		} else if (typePrimaryKeyFields.getNone() != null) {
			return null;
		} else if (typePrimaryKeyFields.getField() == null) {
			return null;
		}
		KeyField[] keyFields = new KeyField[typePrimaryKeyFields.getField()
				.size()];
		int i = 0;
		for (TypeFieldName eachTypeFieldName : typePrimaryKeyFields.getField()) {
			KeyField eachKeyField = new KeyField();
			eachKeyField.setName(eachTypeFieldName.getName());
			// eachKeyField.setSortOrder(eachTypeFieldName.getOrder().value());
			keyFields[i] = eachKeyField;
			i++;
		}
		return keyFields;
	}

	/**
	 * Extracts the key field names and sort order from the
	 * {@link hydrograph.engine.jaxb.sort.TypePrimaryKeyFields
	 * TypePrimaryKeyFields} object passed as a parameter
	 * <p>
	 * The method returns {@code null} if the {@code typePrimaryKeyFields}
	 * parameter is {@code null}
	 * 
	 * @param typePrimaryKeyFields
	 *            the object of
	 *            {@link hydrograph.engine.jaxb.sort.TypePrimaryKeyFields
	 *            TypePrimaryKeyFields} which contain information of key fields
	 *            for the component
	 * @return an array of {@link KeyField} containing the field name and sort
	 *         order
	 */
	public static KeyField[] extractKeyFields(
			hydrograph.engine.jaxb.sort.TypePrimaryKeyFields typePrimaryKeyFields) {
		if (typePrimaryKeyFields == null) {
			return null;
		} else if (typePrimaryKeyFields.getField() == null) {
			return null;
		}
		List<hydrograph.engine.jaxb.sort.TypePrimaryKeyFieldsAttributes> typePKFieldAttributes = typePrimaryKeyFields
				.getField();
		KeyField[] keyFields = new KeyField[typePKFieldAttributes.size()];
		int i = 0;
		for (hydrograph.engine.jaxb.sort.TypePrimaryKeyFieldsAttributes eachPKFieldAttribute : typePKFieldAttributes) {
			KeyField eachPKKeyField = new KeyField();
			eachPKKeyField.setName(eachPKFieldAttribute.getName());
			eachPKKeyField.setSortOrder(eachPKFieldAttribute.getOrder().name());
			keyFields[i] = eachPKKeyField;
			i++;
		}
		return keyFields;
	}

	/**
	 * Extracts the key field names and sort order from the
	 * {@link hydrograph.engine.jaxb.sort.TypeSecondaryKeyFields
	 * TypeSecondaryKeyFields} object passed as a parameter
	 * <p>
	 * The method returns {@code null} if the {@code typeSecondaryKeyFields}
	 * parameter is {@code null}
	 * 
	 * @param typeSecondaryKeyFields
	 *            the object of
	 *            {@link hydrograph.engine.jaxb.sort.TypeSecondaryKeyFields
	 *            TypeSecondaryKeyFields} which contain information of key
	 *            fields for the component
	 * @return an array of {@link KeyField} containing the field name and sort
	 *         order
	 */
	public static KeyField[] extractSecondaryKeyFields(
			hydrograph.engine.jaxb.sort.TypeSecondaryKeyFields typeSecondaryKeyFields) {
		if (typeSecondaryKeyFields == null) {
			return null;
		} else if (typeSecondaryKeyFields.getField() == null) {
			return null;
		}
		List<hydrograph.engine.jaxb.sort.TypeSecondayKeyFieldsAttributes> typeSKFieldAttributes = typeSecondaryKeyFields
				.getField();
		KeyField[] keyFields = new KeyField[typeSKFieldAttributes.size()];
		int i = 0;
		for (hydrograph.engine.jaxb.sort.TypeSecondayKeyFieldsAttributes eachSKFieldAttribute : typeSKFieldAttributes) {
			KeyField eachSecondaryKeyField = new KeyField();
			eachSecondaryKeyField.setName(eachSKFieldAttribute.getName());
			eachSecondaryKeyField.setSortOrder(eachSKFieldAttribute.getOrder()
					.name());
			keyFields[i] = eachSecondaryKeyField;
			i++;
		}
		return keyFields;
	}

	/**
	 * Extracts the {@link Properties} object from the {@link TypeProperties}
	 * object passed as a parameter
	 * <p>
	 * The method returns {@code null} if the {@code typeProperties} parameter
	 * is null
	 * 
	 * @param typeProperties
	 *            the {@link TypeProperties} object which contain information of
	 *            runtime properties for the component
	 * @return a {@link Properties} object
	 */
	public static Properties extractRuntimeProperties(
			TypeProperties typeProperties) {

		if (typeProperties == null) {
			return null;
		} else if (typeProperties.getProperty() == null) {
			return null;
		}
		Properties properties = new Properties();
		// Fetch all the properties passed to operation
		for (Property eachProperty : typeProperties.getProperty()) {
			properties.setProperty(eachProperty.getName(),
					eachProperty.getValue());
		}

		return properties;
	}
}
