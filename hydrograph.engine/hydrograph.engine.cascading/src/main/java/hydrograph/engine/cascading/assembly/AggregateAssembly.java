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
package hydrograph.engine.cascading.assembly;

import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.handlers.AggregateCustomHandler;
import hydrograph.engine.cascading.assembly.handlers.FieldManupulatingHandler;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.OperationFieldsCreator;
import hydrograph.engine.core.component.entity.AggregateEntity;
import hydrograph.engine.core.component.entity.elements.KeyField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.utils.OutSocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class AggregateAssembly extends BaseComponent<AggregateEntity> {

	private static final long serialVersionUID = 8050470302089972525L;

	private AggregateEntity aggregateEntity;
	private static Logger LOG = LoggerFactory.getLogger(AggregateAssembly.class);
	private OperationFieldsCreator<AggregateEntity> operationFieldsCreator;

	public AggregateAssembly(AggregateEntity baseComponentEntity, ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}
	
	private void setOperationClassInCaseExpression() {
		for (int i = 0; i < aggregateEntity.getOperationsList().size(); i++) {
			if (aggregateEntity.getOperationsList().get(i).getOperationClass() == null) {
				aggregateEntity.getOperationsList().get(i)
						.setOperationClass(
								"hydrograph.engine.expression.userfunctions.AggregateForExpression");
			}
		}
	}
	
	@Override
	public void initializeEntity(AggregateEntity assemblyEntityBase) {
		aggregateEntity = assemblyEntityBase;
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(aggregateEntity.toString());
			}
			setOperationClassInCaseExpression();
			for (OutSocket outSocket : aggregateEntity.getOutSocketList()) {
				LOG.trace("Creating aggregate assembly for '" + aggregateEntity.getComponentId() + "' for socket: '"
						+ outSocket.getSocketId() + "' of type: '" + outSocket.getSocketType() + "'");
				operationFieldsCreator = new OperationFieldsCreator<AggregateEntity>(aggregateEntity,
						componentParameters, outSocket);
				createAssemblyForOutSocket(outSocket);
			}
			LOG.debug("Aggregate Assembly: [ InputFields List : "
					+ Arrays.toString(operationFieldsCreator.getOperationalInputFieldsList().toArray())
					+ ", OperationProperties List : "
					+ Arrays.toString(operationFieldsCreator.getOperationalOperationPropertiesList().toArray())
					+ ", OutputFieldsList : "
					+ Arrays.toString(operationFieldsCreator.getOperationalOutputFieldsList().toArray())
					+ " , TransformClass List : "
					+ Arrays.toString(operationFieldsCreator.getOperationalTransformClassList().toArray())
					+ ", PassThrough Fields : " + operationFieldsCreator.getPassThroughFields() + "]");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	private void createAssemblyForOutSocket(OutSocket outSocket) {
		// initialize the out socket fields
		Fields passThroughFields = operationFieldsCreator.getPassThroughFields();
		Map<String, String> mapFields = OutSocketUtils.getMapFieldsFromOutSocket(outSocket.getMapFieldsList());
		Fields operationFields = new Fields(
				OutSocketUtils.getOperationFieldsFromOutSocket(outSocket.getOperationFieldList()));

		
		Pipe sortAggPipe = new Pipe(aggregateEntity.getComponentId()+ aggregateEntity.getOutSocketList().get(0).getSocketId(),
				componentParameters.getInputPipe());

		// perform groupby operation on keys
		Fields keyFields = getFieldsFromKeyFields(aggregateEntity.getKeyFields());
		Fields secondaryKeyFields = getFieldsFromKeyFields(aggregateEntity.getSecondaryKeyFields());

		sortAggPipe = new GroupBy(sortAggPipe, keyFields, secondaryKeyFields);

		FieldManupulatingHandler fieldManupulatingHandler = new FieldManupulatingHandler(
				operationFieldsCreator.getOperationalInputFieldsList(),
				operationFieldsCreator.getOperationalOutputFieldsList(), keyFields, passThroughFields, mapFields,
				operationFields);

		AggregateCustomHandler groupingHandler = new AggregateCustomHandler(
				fieldManupulatingHandler,
				operationFieldsCreator.getOperationalOperationPropertiesList(),
				operationFieldsCreator.getOperationalTransformClassList(),
				operationFieldsCreator.getOperationalExpressionList(),
				extractInitialValues());
		setHadoopProperties(sortAggPipe.getStepConfigDef());
		sortAggPipe = new Every(sortAggPipe, groupingHandler.getInputFields(), groupingHandler, Fields.RESULTS);

		setOutLink(outSocket.getSocketType(), outSocket.getSocketId(), aggregateEntity.getComponentId(), sortAggPipe,
				fieldManupulatingHandler.getOutputFields());
	}

	private String[] extractInitialValues() {
		String[] initialValues = new String[aggregateEntity.getNumOperations()];
		for (int i = 0; i < aggregateEntity.getNumOperations(); i++) {
			initialValues[i] = aggregateEntity.getOperationsList().get(i)
					.getAccumulatorInitialValue();
		}
		return initialValues;
	}

	/**
	 * Creates an object of type {@link Fields} from array of {@link KeyField}
	 * 
	 * @param keyFields
	 *            an array of {@link KeyField} containing the field name and
	 *            sort order
	 * @return an object of type {@link Fields}
	 */
	private Fields getFieldsFromKeyFields(KeyField[] keyFields) {
		if (keyFields == null) {
			return Fields.NONE;
		}

		String[] fieldNames = new String[keyFields.length];
		int i = 0;
		for (KeyField eachField : keyFields) {
			fieldNames[i] = eachField.getName();
			i++;
		}

		Fields fields = new Fields(fieldNames);
		i = 0;
		for (KeyField eachField : keyFields) {
			if (eachField.getSortOrder().equalsIgnoreCase("desc")) {
				fields.setComparator(eachField.getName(), Collections.reverseOrder());
			}
			i++;
		}
		return fields;
	}
}
