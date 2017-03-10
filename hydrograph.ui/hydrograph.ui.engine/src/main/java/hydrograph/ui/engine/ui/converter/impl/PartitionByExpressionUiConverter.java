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

package hydrograph.ui.engine.ui.converter.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeTransformExpression;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.engine.jaxb.operationstypes.PartitionByExpression;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.OperationClassProperty;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.TransformUiConverter;
import hydrograph.ui.graph.model.Container;

/**
 * The class PartitionByExpressionUiConverter
 * 
 * @author Bitwise
 * 
 */

public class PartitionByExpressionUiConverter extends TransformUiConverter {

	private PartitionByExpression partitionByExpression;
	private int outPortCounter = 0;

	public PartitionByExpressionUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new hydrograph.ui.graph.model.components.PartitionByExpression();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		partitionByExpression = (PartitionByExpression) typeBaseComponent;
		if (outPortCounter == 0) {
			outPortCounter = 2;
		}

		propertyMap.put(Constants.OUTPUT_PORT_COUNT_PROPERTY, String.valueOf(outPortCounter));

		propertyMap.put(PropertyNameConstants.OPERATION_CLASS.value(), getOperationClassOrExpression());
		propertyMap.put(PropertyNameConstants.OPERATION_FILEDS.value(), getOperationFields());

		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.PARTITION_BY_EXPRESSION.value());
		uiComponent.setSize(new Dimension(100, ((outPortCounter + 1) * 27) + 16));

	}

	private OperationClassProperty getOperationClassOrExpression() {
		OperationClassProperty operationClassProperty = null;
		String clazz = null;
		if (partitionByExpression.getOperationOrExpression() != null
				&& partitionByExpression.getOperationOrExpression().size() != 0) {
			if (partitionByExpression.getOperationOrExpression().get(0) instanceof TypeTransformOperation) {
				TypeTransformOperation transformOperation = (TypeTransformOperation) partitionByExpression
						.getOperationOrExpression().get(0);
				clazz = transformOperation.getClazz();
				ExpressionEditorData expressionEditorData = new ExpressionEditorData("",
						uiComponent.getComponentName());
				operationClassProperty = new OperationClassProperty(getOperationClassName(clazz), clazz,
						ParameterUtil.isParameter(clazz), false, expressionEditorData);
			} else if (partitionByExpression.getOperationOrExpression().get(0) instanceof TypeTransformExpression) {
				TypeTransformExpression typeTransformExpression = (TypeTransformExpression) partitionByExpression
						.getOperationOrExpression().get(0);
				ExpressionEditorData expressionEditorData = getExpressionEditorData(typeTransformExpression);

				operationClassProperty = new OperationClassProperty(null, null, false, true, expressionEditorData);
			}
		}
		return operationClassProperty;
	}

	private List<String> getOperationFields() {
		List<String> componentOperationFields = new ArrayList<>();
		for (Object object : partitionByExpression.getOperationOrExpression()) {
			if (object instanceof TypeTransformOperation) {
				TypeTransformOperation transformOperation = (TypeTransformOperation) object;
				if (transformOperation.getInputFields() != null) {

					for (TypeInputField inputFileds : transformOperation.getInputFields().getField()) {
						componentOperationFields.add(inputFileds.getName());
					}
				}
			}
		}
		return componentOperationFields;
	}

	protected void getOutPort(TypeOperationsComponent operationsComponent) {
		if (operationsComponent.getOutSocket() != null) {
			for (TypeOperationsOutSocket outSocket : operationsComponent.getOutSocket()) {
				uiComponent.engageOutputPort(outSocket.getId());
				if (outSocket.getPassThroughFieldOrOperationFieldOrExpressionField() != null) {
					propertyMap.put(Constants.PARAM_OPERATION,
							getUiPassThroughOrOperationFieldsOrMapFieldGrid(outSocket));
					createPassThroughAndMappingFieldsForSchemaPropagation(outSocket);
				}
				if (outSocket.getType().equals("out"))
					outPortCounter++;
			}
			if (outPortCounter > 2) {
				incrementPort();
			}

		}
	}

	private void incrementPort() {
		uiComponent.completeOutputPortSettings(outPortCounter);
		uiComponent.unusedPortSettings(1);
	}

}
