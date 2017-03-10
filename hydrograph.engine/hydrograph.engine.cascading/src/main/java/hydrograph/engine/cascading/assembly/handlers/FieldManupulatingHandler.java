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
package hydrograph.engine.cascading.assembly.handlers;

import cascading.tuple.Fields;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FieldManupulatingHandler implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8968542678313899309L;

	protected ArrayList<Fields> operationInputFields;
	protected ArrayList<Fields> operationOutputFields;
	protected Fields passThrough;
	protected Fields keyFields;
	protected Fields mapSourceFields;
	protected Fields mapTargetFields;
	private Fields outputFields;
	protected Map<String, String> mapFields;
	private ArrayList<int[]> outputPositions;

	private Fields inputFields = new Fields();
	private Fields outputOperationFields = new Fields();

	private int[] inputPassThorughPositions;
	private int[] outputPassThorughPositions;
	private int[] mapSourceFieldPositions;
	private int[] mapTargetFieldPositions;
	private ArrayList<int[]> inputPositions;
	private int[] operationFieldsPositions;

	public FieldManupulatingHandler(Fields inputFields, Fields outputFields,
			Fields keyFields, Fields passThrough, Map<String, String> mapFields) {
		this.operationInputFields = new ArrayList<Fields>();
		this.operationOutputFields = new ArrayList<Fields>();

		this.operationInputFields.add(inputFields);
		this.operationOutputFields.add(outputFields);

		this.passThrough = passThrough;
		this.keyFields = keyFields;
		this.mapFields = mapFields;

		extractMapSourceAndTargetFields();
		determineIO();
		determineFieldPositions();
	}

	public FieldManupulatingHandler(ArrayList<Fields> operationInputFields,
			ArrayList<Fields> operationOutputFields, Fields passThrough,
			Map<String, String> mapFields, Fields operationFields) {

		this(operationInputFields, operationOutputFields, null, passThrough,
				mapFields, operationFields);
	}

	public FieldManupulatingHandler(ArrayList<Fields> operationInputFields,
			ArrayList<Fields> operationOutputFields, Fields passThrough) {

		this(operationInputFields, operationOutputFields, null, passThrough,
				null, null);
	}

	public FieldManupulatingHandler(ArrayList<Fields> operationInputFields,
			ArrayList<Fields> operationOutputFields, Fields passThrough,
			Fields operationFields) {
		this(operationInputFields, operationOutputFields, null, passThrough,
				null, operationFields);
	}

	public FieldManupulatingHandler(ArrayList<Fields> operationInputFields,
			ArrayList<Fields> operationOutputFields, Fields keyFields,
			Fields passThrough, Map<String, String> mapFields,
			Fields operationFields) {
		this.operationInputFields = operationInputFields;
		this.operationOutputFields = operationOutputFields;

		this.passThrough = passThrough;
		this.keyFields = keyFields;
		this.mapFields = mapFields;
		this.outputFields = operationFields;

		extractMapSourceAndTargetFields();
		determineIO();
		determineFieldPositions();
	}

	/**
	 * Extracts the source fields & target fields from the {@code mapFields}
	 * {@code HashMap} and stores them into {@code mapSourceFields} &
	 * {@code mapTargetFields} object respectively
	 */
	private void extractMapSourceAndTargetFields() {
		ArrayList<String> mapSourceFieldsList = new ArrayList<String>();
		ArrayList<String> mapTargetFieldsList = new ArrayList<String>();
		for (String key : mapFields.keySet()) {
			mapSourceFieldsList.add(key);
			mapTargetFieldsList.add(mapFields.get(key));
		}

		if (mapSourceFields == null) {
			mapSourceFields = new Fields(
					mapSourceFieldsList.toArray(new String[mapSourceFieldsList
							.size()]));
		} else {
			mapSourceFields.append(new Fields(mapSourceFieldsList
					.toArray(new String[mapSourceFieldsList.size()])));
		}

		if (mapTargetFields == null) {
			mapTargetFields = new Fields(
					mapTargetFieldsList.toArray(new String[mapTargetFieldsList
							.size()]));
		} else {
			mapTargetFields.append(new Fields(mapTargetFieldsList
					.toArray(new String[mapTargetFieldsList.size()])));
		}
	}

	private void determineIO() {
		if (operationInputFields != null && operationInputFields.size() > 0) {
			inputFields = Fields.merge(operationInputFields
					.toArray(new Fields[operationInputFields.size()]));
		}

		if (operationOutputFields != null && operationOutputFields.size() > 0) {
			outputOperationFields = Fields.merge(operationOutputFields
					.toArray(new Fields[operationOutputFields.size()]));
		}

		if (keyFields != null) {
			inputFields = Fields.merge(inputFields, keyFields);
		}

		if (mapSourceFields != null) {
			inputFields = Fields.merge(inputFields, mapSourceFields);
		}

		if (mapTargetFields != null) {
			outputOperationFields = Fields.merge(outputOperationFields, mapTargetFields);
			outputFields = Fields.merge(outputFields, mapTargetFields);
		}

		if (passThrough != null) {
			outputFields = Fields.merge(outputFields, passThrough);
			inputFields = Fields.merge(inputFields, passThrough);
			outputOperationFields = Fields.merge(outputOperationFields, passThrough);
		}
	}

	private void determineFieldPositions() {

		inputPositions = new ArrayList<int[]>();
		if (operationInputFields != null) {
			for (Fields fields : operationInputFields) {
				inputPositions.add(getInputFields().getPos(fields));
			}
		}

		if (outputFields != null) {
			operationFieldsPositions = getOutputFields().getPos(
					outputFields);
		} else {
			operationFieldsPositions = null;
		}

		if (passThrough != null) {
			inputPassThorughPositions = getInputFields().getPos(passThrough);
			outputPassThorughPositions = getOutputFields().getPos(passThrough);
		} else {
			inputPassThorughPositions = null;
			outputPassThorughPositions = null;
		}

		if (mapSourceFields != null) {
			mapSourceFieldPositions = getInputFields().getPos(mapSourceFields);
		} else {
			mapSourceFieldPositions = null;
		}
		if (mapTargetFields != null) {
			mapTargetFieldPositions = getOutputFields().getPos(mapTargetFields);
		} else {
			mapTargetFieldPositions = null;
		}
		
		outputPositions = new ArrayList<int[]>();

		if (operationOutputFields != null) {
			for (Fields fields : operationOutputFields) {
				int [] position = new int[fields.size()];
				for (int j = 0; j < fields.size(); j++) {
					if(getOutputFields().contains(new Fields(fields.get(j)))){
						position[j] = (getOutputFields().getPos(fields.get(j)))	;
					}else{
						position[j] = -1;
					}
				}
				outputPositions.add(position);

				//outputPositions.add(getOperationFields().getPos(fields))
			}
		}

	}

	public Fields getKeyFields() {
		return keyFields;
	}

	public Fields getPassThroughFields() {
		return passThrough;
	}

	public Fields getOutputFields() {
		return outputFields;
	}

	public Fields getInputFields() {
		return inputFields;
	}

	public Fields getOutputOperationFields() {
		return outputOperationFields;
	}

	public int[] getInputPositions(int index) {
		return inputPositions.get(index);
	}
	
	public ArrayList<int[]> getAllInputPositions(){
		return inputPositions;
	}

	/**
	 * @return the field positions of pass through fields in the input row
	 */
	public int[] getInputPassThroughPositions() {
		// Exposing internal arrays directly allows the user to modify some code
		// that could be critical. It is safer to return a copy of the array
		return inputPassThorughPositions == null ? null : inputPassThorughPositions
				.clone();
	}

	public int[] getOutputPassThroughPositions() {
		// Exposing internal arrays directly allows the user to modify some code
		// that could be critical. It is safer to return a copy of the array
		return outputPassThorughPositions == null ? null : outputPassThorughPositions
				.clone();
	}
	/**
	 * @return the field positions of pass through fields in the input row
	 */
	public int[] getOperationPositions() {
		return operationFieldsPositions != null ? operationFieldsPositions.clone() : null;
	}

	public int[] getInputPositions() {
		return getInputPositions(0);
	}
	
	public int[] getOutputPositions(int index) {
		return outputPositions.get(index);
	}

	public ArrayList<int[]> getAllOutputPositions() {
		return outputPositions;
	}
	
	public void setAllOutputPositions(ArrayList<int[]> outputPositions) {
		this.outputPositions = outputPositions;
	}


	/**
	 * @return the field positions of operation input fields in the input row
	 */
	public ArrayList<Fields> getOperationInputFields() {
		return operationInputFields;
	}

	/**
	 * @return the field positions of operation output fields in the input row
	 */
	public ArrayList<Fields> getOperationOutputFields() {
		return operationOutputFields;
	}

	/**
	 * @return the field positions of source fields in map fields in the input
	 *         row
	 */
	public int[] getMapSourceFieldPositions() {
		return mapSourceFieldPositions != null ? mapSourceFieldPositions.clone() : null;
	}
	
	public int[] getMapTargetFieldPositions() {
		return mapTargetFieldPositions != null ? mapTargetFieldPositions.clone() : null;
	}

	/**
	 * @return the a map object {@link Map}<{@link String}, {@link String}>
	 *         containing all the map field names. The map fields are stored as
	 *         Map&lt;SourceFieldName, TargetFieldName&gt;
	 */
	public Map<String, String> getMapFields() {
		return mapFields;
	}

	/**
	 * @param mapFields
	 *            the mapFields to set. The map fields are stored as
	 *            Map&lt;SourceFieldName, TargetFieldName&gt;
	 */
	public void setMapFields(Map<String, String> mapFields) {
		this.mapFields = mapFields;
	}

	/**
	 * @return the source fields for map fields
	 */
	public Fields getMapSourceFields() {
		return mapSourceFields;
	}

	/**
	 * @param mapSourceFields
	 *            the mapSourceFields to set
	 */
	public void setMapSourceFields(Fields mapSourceFields) {
		this.mapSourceFields = mapSourceFields;
	}

	/**
	 * @return a list of map source fields
	 */
	public List<String> getMapSourceFieldsAsList() {
		if (mapSourceFields == null) {
			return null;
		}
		ArrayList<String> mapSourceFieldsList = new ArrayList<String>();
		for (int i = 0; i < mapSourceFields.size(); i++) {
			mapSourceFieldsList.add(mapSourceFields.get(i).toString());
		}

		return mapSourceFieldsList;
	}

	/**
	 * @return the target fields for map fields
	 */
	public Fields getMapTargetFields() {
		return mapTargetFields;
	}

	/**
	 * @param mapTargetFields
	 *            the mapTargetFields to set
	 */
	public void setMapTargetFields(Fields mapTargetFields) {
		this.mapTargetFields = mapTargetFields;
	}
}
