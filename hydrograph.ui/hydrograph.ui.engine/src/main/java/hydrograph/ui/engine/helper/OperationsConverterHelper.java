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

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeOperationInputFields;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeTransformExpression;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.OperationClassProperty;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
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
	
	public List<Object> getOperations(String id) {
		logger.debug("Generating TypeTransformOperation data :{}", properties.get(Constants.PARAM_NAME));
        List<Object> operationList = new ArrayList<>();
     	OperationClassProperty operationClassProperty=(OperationClassProperty) properties.get(PropertyNameConstants.OPERATION_CLASS.value());
		
		if(operationClassProperty!=null && operationClassProperty.isExpression())
		{
			TypeTransformExpression typeTransformExpression=new TypeTransformExpression();
			TypeOperationInputFields operationInputFields = new TypeOperationInputFields();
			operationInputFields.getField().addAll(getExpressionInputField(operationClassProperty.getExpressionEditorData(), id));
			typeTransformExpression.setInputFields(operationInputFields);
			typeTransformExpression.setId(OPERATION_ID);
			
			typeTransformExpression.setExpr(operationClassProperty.getExpressionEditorData().getExpression());
			operationList.add(typeTransformExpression);
		   
			
		}	
		else if(operationClassProperty!=null)
		{
		TypeTransformOperation operation = new TypeTransformOperation();
		TypeOperationInputFields operationInputFields = new TypeOperationInputFields();
		operationInputFields.getField().addAll(getOperationField(id));
		operation.setInputFields(operationInputFields);
		operation.setId(OPERATION_ID);
		
			operation.setClazz(((OperationClassProperty) properties.get(PropertyNameConstants.OPERATION_CLASS.value()))
					.getOperationClassPath());
		operationList.add(operation);
		}
		return operationList;
	}
	
	
	private List<TypeInputField> getOperationField(String id) {
		logger.debug("Generating TypeInputField data :{}", properties.get(Constants.PARAM_NAME));
		List<TypeInputField> operationFiledList = new ArrayList<>();
		List<String> componentOperationFields = (List<String>) component.getProperties().get(
				PropertyNameConstants.OPERATION_FILEDS.value());
		if (componentOperationFields != null && !componentOperationFields.isEmpty()) {
			if (!converterHelper.hasAllStringsInListAsParams(componentOperationFields)) {
				for (String fieldName : componentOperationFields) {
					if (!ParameterUtil.isParameter(fieldName)) {
						TypeInputField operationField = new TypeInputField();
						operationField.setName(fieldName);
						operationField.setInSocketId(Constants.FIXED_INSOCKET_ID);
						operationFiledList.add(operationField);
					} else {
						converterHelper.addParamTag(id, fieldName,	
								ComponentXpathConstants.OPERATION_INPUT_FIELDS.value(),false);
					}
				}
			} else {
				StringBuffer parameterFieldNames=new StringBuffer();
				TypeInputField operationField = new TypeInputField();
				operationField.setName("");
				operationFiledList.add(operationField);
				for (String fieldName : componentOperationFields){ 
					parameterFieldNames.append(fieldName+ " ");
				}
				converterHelper.addParamTag(id, parameterFieldNames.toString(), 
						ComponentXpathConstants.OPERATION_INPUT_FIELDS.value(),true);
			}
		}
		return operationFiledList;
	}

	
	   public List<TypeInputField> getExpressionInputField(ExpressionEditorData expressionEditorData , String id)
	    {
	    	logger.debug("Generating TypeInputField data :{}", properties.get(Constants.PARAM_NAME));
	    	List<String> listOfFieldsUsedInExpression=expressionEditorData.getfieldsUsedInExpression();
	    	List<TypeInputField> expressionFiledList = new ArrayList<>();
			List<String> componentOperationFields = listOfFieldsUsedInExpression;
			if (componentOperationFields != null && !componentOperationFields.isEmpty()) {
				if (!converterHelper.hasAllStringsInListAsParams(componentOperationFields)) {
					for (String fieldName : componentOperationFields) {
						if (!ParameterUtil.isParameter(fieldName)) {
							
							TypeInputField operationField = new TypeInputField();
							operationField.setName(fieldName);
							operationField.setInSocketId(Constants.FIXED_INSOCKET_ID);
							expressionFiledList.add(operationField);
						} else {
							converterHelper.addParamTag(id, fieldName,	
									ComponentXpathConstants.OPERATION_INPUT_FIELDS.value(),false);
						}
					}
				} else {
					StringBuffer parameterFieldNames=new StringBuffer();
					TypeInputField operationField = new TypeInputField();
					operationField.setName("");
					expressionFiledList.add(operationField);
					for (String fieldName : componentOperationFields){ 
						parameterFieldNames.append(fieldName+ " ");
					}
					converterHelper.addParamTag(id, parameterFieldNames.toString(), 
							ComponentXpathConstants.OPERATION_INPUT_FIELDS.value(),true);
				}
			}
			return expressionFiledList;
	    	
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
