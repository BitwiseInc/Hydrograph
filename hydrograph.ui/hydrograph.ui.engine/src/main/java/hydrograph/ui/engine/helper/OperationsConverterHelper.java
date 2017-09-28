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
package hydrograph.ui.engine.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeOperationInputFields;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeTransformExpression;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.qnames.OperationsExpressionType;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

public class OperationsConverterHelper {
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OperationsConverterHelper.class);
	private static final String OPERATION_ID = "operation_1";
	private ConverterHelper converterHelper;
	protected Component component = null;
	protected Map<String, Object> properties = new LinkedHashMap<String, Object>();
	protected String componentName = null;
	
	public OperationsConverterHelper(Component component) {
		this.component = component;
		this.properties = component.getProperties();
		this.componentName = (String) properties.get(Constants.PARAM_NAME);
		converterHelper = new ConverterHelper(component);
	}

	
	public List<TypeOperationsOutSocket> getOutSocket() {

		logger.debug("Generating TypeStraightPullOutSocket data for : {}", properties.get(Constants.PARAM_NAME));
		List<TypeOperationsOutSocket> outSockectList = new ArrayList<TypeOperationsOutSocket>();
		for (Link link : component.getSourceConnections()) {
			TypeOperationsOutSocket outSocket = new TypeOperationsOutSocket();
			TypeOutSocketAsInSocket outSocketAsInsocket = new TypeOutSocketAsInSocket();
			outSocketAsInsocket.setInSocketId(Constants.FIXED_INSOCKET_ID);
			outSocketAsInsocket.getOtherAttributes();
			outSocket.setCopyOfInsocket(outSocketAsInsocket);

			outSocket.setId(link.getSourceTerminal());
			outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());

			outSocket.getOtherAttributes();
			outSockectList.add(outSocket);
		}
		return outSockectList;

	}
	
	/**
	 * @param id
	 * @return
	 */
	public List<JAXBElement<?>> getFilterOperations(String id) {
		logger.debug("Generating TypeTransformOperation data :{}", properties.get(Constants.PARAM_NAME));
		List<JAXBElement<?>> operationList = new ArrayList<>();
		FilterLogicDataStructure filterData = (FilterLogicDataStructure) properties.get("filterLogic");
		if (filterData != null && filterData.isOperation() ) {
			if (filterData.getOperationClassData().getExternalOperationClassData().isExternal()) {
				converterHelper.addExternalJaxbOperation(operationList,
						filterData.getOperationClassData().getExternalOperationClassData());
			} else {
				TypeTransformOperation operation = new TypeTransformOperation();
				addFilterOperationInputFields(filterData, operation);
				operation.setId(filterData.getOperationClassData().getId());
				operation.setClazz(filterData.getOperationClassData().getQualifiedOperationClassName());
				// Added the below line for passing the properties for Filter
				// operation
				if (!filterData.getOperationClassData().getClassProperties().isEmpty()) {
					operation.setProperties(converterHelper
							.getOperationProperties(filterData.getOperationClassData().getClassProperties()));
				}
				
				JAXBElement<TypeTransformOperation> jaxbElement = new JAXBElement(OperationsExpressionType.OPERATION.getQName(),
						TypeTransformOperation.class, operation);
				operationList.add(jaxbElement);
				
			}
		} else if (filterData != null) {
			if (filterData.getExpressionEditorData().getExternalExpressionData().isExternal()) {
				converterHelper.addExternalJaxbExpression(operationList,
						filterData.getExpressionEditorData().getExternalExpressionData());

			} else {
				TypeTransformExpression typeTransformExpression = new TypeTransformExpression();
				addFilterExpressionInputField(filterData.getExpressionEditorData(), typeTransformExpression);
				typeTransformExpression.setId(filterData.getExpressionEditorData().getId());

				typeTransformExpression
						.setExpr(filterData.getExpressionEditorData().getExpression());

				JAXBElement<TypeTransformExpression> jaxbElement = new JAXBElement(OperationsExpressionType.EXPRESSION.getQName(),
						TypeTransformExpression.class, typeTransformExpression);
				operationList.add(jaxbElement);
			}
		}

		return operationList;
	}


	private void addFilterOperationInputFields(FilterLogicDataStructure filterData, TypeTransformOperation operation) {
		if (filterData.getOperationClassData() != null
				&& filterData.getOperationClassData().getInputFields() != null
				&& !filterData.getOperationClassData().getInputFields().isEmpty()) {

			
			TypeOperationInputFields operationInputFields = new TypeOperationInputFields();
			operationInputFields.getField().addAll(getOperationField(filterData.getOperationClassData().getInputFields()));
			operation.setInputFields(operationInputFields);
		}
	}

	
	private List<TypeInputField> getOperationField(List<String> inputFields) {
		logger.debug("Generating TypeInputField data :{}", properties.get(Constants.PARAM_NAME));
		List<TypeInputField> operationFiledList = new ArrayList<>();
		if(inputFields!=null){
		for (String fieldName : inputFields) {
			TypeInputField operationField = new TypeInputField();
			operationField.setName(fieldName);
			operationField.setInSocketId(Constants.FIXED_INSOCKET_ID);
			operationFiledList.add(operationField);
		}
		}
		return operationFiledList;
	}

	
	public void addFilterExpressionInputField(ExpressionData expressionData,
			TypeTransformExpression transformExpression) {
		if (expressionData != null && !expressionData.getInputFields().isEmpty()) {
			TypeOperationInputFields operationInputFields = new TypeOperationInputFields();
			for (String fieldName : expressionData.getInputFields()) {
				if (StringUtils.isNotBlank(fieldName)) {
					TypeInputField field = new TypeInputField();
					field.setName(fieldName);
					field.setInSocketId(Constants.FIXED_INSOCKET_ID);
					operationInputFields.getField().add(field);
				}
			}
			transformExpression.setInputFields(operationInputFields);
		}
	}
	   
	   public List<TypeBaseInSocket> getInSocket() {
			logger.debug("Generating TypeBaseInSocket data for :{}", component.getProperties().get(Constants.PARAM_NAME));
			List<TypeBaseInSocket> inSocketsList = new ArrayList<>();
			for (Link link : component.getTargetConnections()) {
				TypeBaseInSocket inSocket = new TypeBaseInSocket();
				inSocket.setFromComponentId(link.getSource().getComponentId());
				inSocket.setFromSocketId(converterHelper.getFromSocketId(link));
				inSocket.setFromSocketType(link.getSource().getPorts().get(link.getSourceTerminal()).getPortType());
				inSocket.setId(link.getTargetTerminal());
				inSocket.setType(link.getTarget().getPort(link.getTargetTerminal()).getPortType());
				inSocket.getOtherAttributes();
				inSocketsList.add(inSocket);
			}
			return inSocketsList;
		}
		
	    


}
