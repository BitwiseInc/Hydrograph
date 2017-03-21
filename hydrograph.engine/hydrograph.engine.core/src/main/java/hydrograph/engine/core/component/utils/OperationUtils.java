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
package hydrograph.engine.core.component.utils;

import hydrograph.engine.core.component.entity.base.OperationEntityBase;
import hydrograph.engine.core.component.entity.elements.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * The Class OperationUtils.
 *
 * @author Bitwise
 *
 */
public class OperationUtils {

	public static List<String> getAllFields(List<OutSocket> outSocketsList, List<String> inputSchema) {
		List<String> outFields = new ArrayList<>();
		for (OutSocket outSocket : outSocketsList) {
			outFields.addAll(getPassThrougFields(outSocket.getPassThroughFieldsList(), inputSchema));

			for (MapField mapField : outSocket.getMapFieldsList()) {
				outFields.add(mapField.getName());
			}

			for (OperationField op : outSocket.getOperationFieldList()) {
				outFields.add(op.getName());
			}
		}
		return outFields;
	}

	public static List<String> getAllFieldsWithOperationFields(OperationEntityBase entity, List<String> inputSchema) {
		List<String> outFields = new ArrayList<>();
		outFields.addAll(inputSchema);
		if (null != entity.getOperationsList() && entity.getOperationsList().size() > 0) {
			for (Operation operation : entity.getOperationsList()) {
                if(operation.getOperationOutputFields()!=null)
                    for (String field : operation.getOperationOutputFields())
					    if (!inputSchema.contains(field))
						outFields.add(field);
			}
		}
		return outFields;
	}

	public static List<String> getPassThrougFields(List<PassThroughField> passThroughFieldList,
			List<String> inputSchemaList) {
		Set<String> passThroughFields = new HashSet<>();
		for (PassThroughField passThrough : passThroughFieldList) {
			if (passThrough.getName().contains("*")) {
				return inputSchemaList;
			}
			passThroughFields.add(passThrough.getName());
		}
		return new ArrayList<String>(passThroughFields);
	}
}
