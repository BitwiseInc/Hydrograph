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
package hydrograph.engine.cascading.assembly.utils;

import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.elements.MapField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.PassThroughField;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JoinHelper {
	ComponentParameters componentParameters = new ComponentParameters();

	public JoinHelper(ComponentParameters params) {
		super();
		this.componentParameters = params;
	}

	Map<String, Map<Fields, Fields>> fileMap;

	@SuppressWarnings("rawtypes")
	public Fields getMapTargetFields(OutSocket outSocket) {

		Set<Fields> rawTargetFields = getRawTargetFields(outSocket);
		Fields targetFields = new Fields();

		fileMap = getFieldsMap(outSocket);

		if (fileMap == null) {
			return null;
		}
		int i = 0;

		for (Entry<String, Map<Fields, Fields>> fieldsMapEntry : fileMap
				.entrySet()) {
			Map<Fields, Fields> fieldsMap = fieldsMapEntry.getValue();
			String socketId = fieldsMapEntry.getKey();
			Fields inputFields = null;
			for (int j = 0; j < componentParameters.getinSocketId().size(); j++) {
				if (socketId
						.equalsIgnoreCase(componentParameters.getinSocketId().get(j))) {
					inputFields = componentParameters.getInputFieldsList().get(j);
				}
			}

			for (Entry<Fields, Fields> e : fieldsMap.entrySet()) {
				Fields fields = e.getKey();
				String field = fields.get(0).toString();
				// get one to one field mapping
				if (!field.equals("*") && !field.endsWith(".*"))
					if (targetFields.contains(fields)) {
						targetFields = targetFields.append(fields.rename(
								fields, new Fields("in" + i + "." + fields)));
					} else {
						targetFields = targetFields.append(fields);
					}

				// get wildcard field mapping
				if (field.endsWith(".*") || field.equals("*")) {
					String targetPrefix = field.replaceAll("\\*$", "");
					String sourcePrefix = e.getValue().get(0).toString()
							.replaceAll("\\*$", "");

					for (Comparable inputFieldName : inputFields) {
						if (inputFieldName.toString().startsWith(sourcePrefix)) {
							String fieldName = inputFieldName.toString()
									.replaceAll("^" + sourcePrefix,
											targetPrefix);

							if (!rawTargetFields
									.contains(new Fields(fieldName))
									&& !fieldsMap.values().contains(
											new Fields(inputFieldName))) {

								targetFields = targetFields.append(new Fields(
										fieldName));
							}
						}
					}
				}
			}

			i++;
		}

		return targetFields;

	}

	private Map<String, Map<Fields, Fields>> getFieldsMap(OutSocket outSocket) {
		Map<String, Map<Fields, Fields>> fieldAndInSocketIdMap = new LinkedHashMap<String, Map<Fields, Fields>>();
		Map<Fields, Fields> fieldsMaps = null;

		for (String socketID : componentParameters.getinSocketId()) {
//			Check if outSocket contains copyOfInSocketId
			if (outSocket.getMapFieldsList().size() == 0
					&& outSocket.getPassThroughFieldsList().size() == 0
					&& outSocket.getCopyOfInSocketId() != null) {
				if (fieldAndInSocketIdMap.get(socketID) == null) {
					fieldsMaps = new LinkedHashMap<Fields, Fields>();
				}
				
					if (socketID
							.equals(outSocket.getCopyOfInSocketId())) {
						Fields copyFieldsOfInSocketId = componentParameters
								.getCopyOfInSocket(socketID);
						for (int i = 0; i < copyFieldsOfInSocketId.size(); i++) {
							fieldsMaps.put(
									new Fields(copyFieldsOfInSocketId.get(i)),
									new Fields(copyFieldsOfInSocketId.get(i)));
							fieldAndInSocketIdMap.put(socketID, fieldsMaps);
						}
				}

			} else {
//				Populate map for mapFields and passThroughFields
				for (MapField mapField : outSocket.getMapFieldsList()) {
					if (fieldAndInSocketIdMap.get(mapField.getInSocketId()) == null) {
						fieldsMaps = new LinkedHashMap<Fields, Fields>();
					}
					if (mapField.getInSocketId().equalsIgnoreCase(socketID)) {

						fieldsMaps.put(new Fields(mapField.getName()),
								new Fields(mapField.getSourceName()));
						fieldAndInSocketIdMap.put(mapField.getInSocketId(), fieldsMaps);
					}

				}

				for (PassThroughField passThroughField : outSocket
						.getPassThroughFieldsList()) {

					if (passThroughField.getInSocketId().equalsIgnoreCase(
							socketID)) {
						if (fieldAndInSocketIdMap.get(passThroughField.getInSocketId()) == null) {
							fieldsMaps = new LinkedHashMap<Fields, Fields>();
						} else {
							fieldsMaps = fieldAndInSocketIdMap.get(passThroughField
									.getInSocketId());
						}
						fieldsMaps.put(new Fields(passThroughField.getName()),
								new Fields(passThroughField.getName()));
						fieldAndInSocketIdMap.put(passThroughField.getInSocketId(),
								fieldsMaps);
					}

				}

			}
		}

		// }
		return fieldAndInSocketIdMap;
	}

	@SuppressWarnings("rawtypes")
	public Fields getMapSourceFields(String inSocketId, OutSocket outSocket) {
		Fields sourceFields = new Fields();

		Map<Fields, Fields> fieldMap = getMapFields(outSocket, inSocketId);

		Set<Fields> rawTargetFields = getRawTargetFields(outSocket);
		Set<Fields> rawSourceFields = getRawSourceFields(fieldMap);
		Fields inputFields = componentParameters.getCopyOfInSocket(inSocketId);

		if (fieldMap == null) {
			return null;
		}

		for (Fields fields : fieldMap.keySet()) {
			String field = fields.get(0).toString();

			// get one to one field mapping
			if (!field.equals("*") && !field.endsWith(".*"))
				sourceFields = sourceFields.append(fields);

			// get wildcard field mapping
			if (field.endsWith(".*") || field.equals("*")) {
				String prefix = field.replaceAll("\\*$", "");
				String targetPrefix = "";
				targetPrefix = getKeyFromValue(fieldMap, field);
				targetPrefix = targetPrefix.replace("*", "");

				for (Comparable inputFieldName : inputFields) {
					if (inputFieldName.toString().startsWith(prefix)) {
						if (!rawSourceFields
								.contains(new Fields(inputFieldName))
								&& !rawTargetFields.contains(new Fields(
										targetPrefix + inputFieldName))) {
							sourceFields = sourceFields.append(new Fields(
									inputFieldName));
						}
					}
				}
			}

		}

		return sourceFields;
	}

	public static String getKeyFromValue(Map<Fields, Fields> fieldsMap,
			String value) {

		for (Entry<Fields, Fields> e : fieldsMap.entrySet()) {
			if (e.getKey().get(0).equals(value)) {
				return (String) e.getValue().get(0);
			}
		}

		return null;
	}

	private Set<Fields> getRawSourceFields(Map<Fields, Fields> fieldMap) {
		Set<Fields> rawSourceFields = new LinkedHashSet<Fields>();

		if (fieldMap == null) {
			return null;
		}

		rawSourceFields.addAll(fieldMap.keySet());

		return rawSourceFields;
	}

	private Set<Fields> getRawTargetFields(OutSocket outSocket) {
		Set<Fields> rawTargetFields = new LinkedHashSet<Fields>();
		for (PassThroughField passthroughFields : outSocket
				.getPassThroughFieldsList()) {
			rawTargetFields.add(new Fields(passthroughFields.getName()));
		}

		return rawTargetFields;
	}

	private Map<Fields, Fields> getMapFields(OutSocket outSocket,
			String inSocketId) {
		Map<Fields, Fields> fieldMap = new LinkedHashMap<Fields, Fields>();
//		Check if outSocket contains copyOfInSocketId 
		if (outSocket.getMapFieldsList().size() == 0
				&& outSocket.getPassThroughFieldsList().size() == 0
				&& outSocket.getCopyOfInSocketId() != null) {
			if (outSocket.getCopyOfInSocketId().equals(inSocketId)){
				Fields copyFieldsOfInSocketId = componentParameters
						.getCopyOfInSocket(inSocketId);
				for (int i = 0; i < copyFieldsOfInSocketId.size(); i++) {
					fieldMap.put(new Fields(copyFieldsOfInSocketId.get(i)),
							new Fields(copyFieldsOfInSocketId.get(i)));
				}
			}
		} else {
//			Populate map for mapFields and passThroughFields 
			for (MapField mapField : outSocket.getMapFieldsList()) {
				if (mapField.getInSocketId().equalsIgnoreCase(inSocketId)) {
					fieldMap.put(new Fields(mapField.getSourceName()),
							new Fields(mapField.getName()));
				}
			}
			for (PassThroughField passthroughFields : outSocket
					.getPassThroughFieldsList()) {
				if (passthroughFields.getInSocketId().equalsIgnoreCase(
						inSocketId)) {
					if (fieldMap.containsKey(new Fields(passthroughFields
							.getName()))) {
						throw new RuntimeException(
								"Pass Through Fields:'"
										+ passthroughFields.getName()
										+ "' is already defined as Map Field in Out Socket '"
										+ outSocket.getSocketId()
										+ "' of join component");
					} else {
						fieldMap.put(new Fields(passthroughFields.getName()),
								new Fields(passthroughFields.getName()));
					}

				}

			}
		}
		return fieldMap;
	}

}
