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
 * limitations under the License.
 *******************************************************************************/

package hydrograph.ui.engine.xpath;

/**
 * The enum ComponentXpathConstants
 * 
 * @author Bitwise
 * 
 */
public enum ComponentXpathConstants {
	GRAPH_XPATH("/graph"),
	COMPONENT_CHARSET_XPATH("/graph/*[@id='$id']/charset"),
	COMPONENT_WRITEMODE_XPATH("/graph/*[@id='$id']/writeMode"),
	COMPONENT_JOIN_TYPE_XPATH("/graph/operations[@id='$id']/keys[@inSocketId='$inSocketId'] [not(@joinType)]"),
	COMPONENT_XPATH_BOOLEAN("/graph/*[@id='$id']/propertyName"),
	COMPONENT_XPATH_COUNT("/graph/*[@id='$id']/maxRecords"),
	COMPONENT_NO_OF_RECORDS_COUNT("/graph/*[@id='$id']/recordCount"),
	
	OPERATION_INPUT_FIELDS("/graph/operations[@id='$id']/operation[@id='operation_1']/inputFields"),
	
	OPERATIONS_PRIMARY_KEYS("/graph/operations[@id='$id']/primaryKeys"),
	OPERATIONS_SECONDARY_KEYS("/graph/operations[@id='$id']/secondaryKeys"),
	
	STRAIGHTPULL_PRIMARY_KEYS("/graph/straightPulls[@id='$id']/primaryKeys"),
	STRAIGHTPULL_SECONDARY_KEYS("/graph/straightPulls[@id='$id']/secondaryKeys"),
	
	EXCEL_PRIMARY_KEYS("/graph/excelsortKeys[@id='$id']/primaryKeys"),
	
	LOOKUP_KEYS("/graph/operations[@id='$id']/keys[@inSocketId='$inSocketId']"),
	JOIN_KEYS("/graph/operations[@id='$id']/keys[@inSocketId='$inSocketId']"),
	
	RUNTIME_PROPERTIES("/graph/*[@id='$id']/runtimeProperties"),
	
	OPERATIONS_OUTSOCKET("graph/operations[@id='$id']/outSocket"),
	
	TRANSFORM_INPUT_FIELDS("/graph/operations[@id='$id']/operation[@id='$operationId']/inputFields"),
	TRANSFORM_OUTPUT_FIELDS("/graph/operations[@id='$id']/operation[@id='$operationId']/outputFields"),
	
	TRANSFORM_OPERATION("graph/operations[@id='$id']");
	
	
	private final String value;

	ComponentXpathConstants(String value) {
		this.value = value;
	}

	public String value() { 
		return value;
	}

	public static ComponentXpathConstants fromValue(String value) {
		for (ComponentXpathConstants xpathConstants : ComponentXpathConstants.values()) {
			if (xpathConstants.value.equals(value)) {
				return xpathConstants;
			}
		}
		throw new IllegalArgumentException(value);
	}

}
