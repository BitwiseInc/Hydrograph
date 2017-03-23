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

import hydrograph.engine.core.component.entity.elements.MapField;
import hydrograph.engine.core.component.entity.elements.OperationField;
import hydrograph.engine.core.component.entity.elements.PassThroughField;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * The Class OutSocketUtils.
 *
 * @author Bitwise
 *
 */

public class OutSocketUtils implements Serializable{

	/**
	 * Creates an object of an array of {@link String} from the list of
	 * {@link OperationField}
	 * 
	 * @param operationFieldList
	 *            the list of {@link OperationField} which contains information
	 *            of Operation Field of out socket of component
	 *            <p>
	 *            The method returns {@code null} if the
	 *            {@code operationFieldList} parameter is null
	 * @return an array of {@link String}
	 */
	public static String[] getOperationFieldsFromOutSocket(List<OperationField> operationFieldList) {
		String[] opFields = null;
		if (operationFieldList != null) {
			opFields = new String[operationFieldList.size()];
			int i = 0;
			for (OperationField operationFields2 : operationFieldList) {
				opFields[i++] = operationFields2.getName();
			}
		}
		return opFields;
	}

	/**
	 * Creates an object of an array of {@link String} from the list of
	 * {@link PassThroughField}
	 * 
	 * @param passThroughFieldsList
	 *            the list of {@link PassThroughField} which contains
	 *            information of Pass Through Field of out socket of component
	 *            <p>
	 *            The method returns {@code null} if the
	 *            {@code passThroughFieldsList} parameter is null
	 * @return an array of {@link String}
	 */

	
	public static String[] getPassThroughFieldsFromOutSocket(List<PassThroughField> passThroughFieldsList,
			String[] allInputFields) {
		String[] passFields = null;
		if (passThroughFieldsList != null) {

			boolean areAllPassFields = OutSocketUtils.checkIfAllFieldsArePassthrough(passThroughFieldsList);
			
			if (areAllPassFields) {
				passFields = new String[allInputFields.length];
				for (int i = 0; i < allInputFields.length; i++) {
					passFields[i] = allInputFields[i].toString();
				}
			} else {
				passFields = new String[passThroughFieldsList.size()];
				int i = 0;
				for (PassThroughField passThroughFields2 : passThroughFieldsList) {
					passFields[i++] = passThroughFields2.getName();
				}
			}
		}
		return passFields;
	}


	private static boolean checkIfAllFieldsArePassthrough(List<PassThroughField> passThroughFieldsList) {
		boolean result = false;
		for (PassThroughField currentField : passThroughFieldsList) {

			if (currentField.getName().equals("*")) {
				result = true;
				break;
			}

		}
		return result;
	}

	/**
	 * Returns a {@link Map}<{@link String}, {@link String}> containing the
	 * source and target map fields from the list of {@link MapField}
	 * 
	 * @param mapFieldsList
	 *            the list of {@link MapField} which contains information of map
	 *            fields for out socket of component
	 *            <p>
	 *            The method returns {@code null} if the {@code mapFieldsList}
	 *            parameter is null
	 * @return Returns a {@link Map}<{@link String}, {@link String}> containing the
	 * source and target map fields
	 */
	public static Map<String, String> getMapFieldsFromOutSocket(List<MapField> mapFieldsList) {
		Map<String, String> mapFields = new LinkedHashMap<String, String>();
		if (mapFieldsList == null) {
			mapFields = null;
		} else {
			for (MapField mapField : mapFieldsList) {
				mapFields.put(mapField.getSourceName(), mapField.getName());
			}
		}
		return mapFields;
	}
}
