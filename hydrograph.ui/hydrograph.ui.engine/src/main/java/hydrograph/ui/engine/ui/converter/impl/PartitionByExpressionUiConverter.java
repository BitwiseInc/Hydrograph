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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Dimension;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeTransformExpression;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.engine.jaxb.operationstypes.PartitionByExpression;
import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.common.datastructure.filter.OperationClassData;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.FilterLogicExternalOperationExpressionUtil;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
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
		propertyMap.put(PropertyNameConstants.FILTER_LOGIC.value(), getOperationClassOrExpression());
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.PARTITION_BY_EXPRESSION.value());
		uiComponent.setSize(new Dimension(100, ((outPortCounter + 1) * 27) + 16));

	}

	private FilterLogicDataStructure getOperationClassOrExpression() {
		FilterLogicDataStructure filterLogicDataStructure =new FilterLogicDataStructure(uiComponent.getComponentName());
		if(partitionByExpression.getOperationOrExpressionOrIncludeExternalOperation()!=null && !partitionByExpression.getOperationOrExpressionOrIncludeExternalOperation().isEmpty())
		{
			List<JAXBElement<?>> typeTransformOpertaionList = ((TypeOperationsComponent) partitionByExpression)
					.getOperationOrExpressionOrIncludeExternalOperation();
			
			if (((JAXBElement<?>) typeTransformOpertaionList.get(0)).getValue() instanceof TypeExternalSchema) {
				populateUIDataFromExternalData(filterLogicDataStructure, typeTransformOpertaionList);	 
			    	 
			}
			
			else if (((JAXBElement<?>) typeTransformOpertaionList.get(0)).getValue() instanceof TypeTransformOperation) 
		    {	
		    	TypeTransformOperation transformOperation=(TypeTransformOperation) partitionByExpression
		    			.getOperationOrExpressionOrIncludeExternalOperation().get(0).getValue();
		    	
		    	OperationClassData operationClassData = new OperationClassData();
		    	operationClassData.setId(transformOperation.getId());
		    	operationClassData.setQualifiedOperationClassName(transformOperation.getClazz());
		    	operationClassData.getInputFields().addAll(getOperationFileds(transformOperation));
		    	operationClassData.getClassProperties().addAll(getProperties(transformOperation));
		    	filterLogicDataStructure.setOperation(true);
		    	filterLogicDataStructure.setOperationClassData(operationClassData);
		    }
			else if (((JAXBElement<?>) typeTransformOpertaionList.get(0)).getValue() instanceof TypeTransformExpression) 
		    {
		     TypeTransformExpression typeTransformExpression=(TypeTransformExpression)partitionByExpression.getOperationOrExpressionOrIncludeExternalOperation().get(0).getValue();
		     ExpressionData expressionData = new ExpressionData(uiComponent.getComponentName());
		     expressionData.setId(typeTransformExpression.getId());
		     ExpressionEditorData expressionEditorData=getExpressionEditorData(typeTransformExpression);
		     expressionData.setExpressionEditorData(expressionEditorData);
		     for(TypeInputField inputField : typeTransformExpression.getInputFields().getField()){
				 expressionData.getInputFields().add( inputField.getName());
			 }
			 filterLogicDataStructure.setExpressionEditorData(expressionData);
			 filterLogicDataStructure.setOperation(false);
		    }
		}
		return filterLogicDataStructure;
	}

	/**
	 * initialize ui object from external file data.
	 * 
	 * @param filterLogicDataStructure
	 * @param typeTransformOpertaionList
	 */
	public void populateUIDataFromExternalData(FilterLogicDataStructure filterLogicDataStructure,
			List<JAXBElement<?>> typeTransformOpertaionList) {
		TypeExternalSchema typeExternalSchema=(TypeExternalSchema) partitionByExpression
				.getOperationOrExpressionOrIncludeExternalOperation().get(0).getValue();
		String filePath = typeExternalSchema.getUri();
		filePath = StringUtils.replace(filePath, "../","");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		 IPath relativePath=null;
			 relativePath=workspace.getRoot().getFile(new Path(filePath)).getLocation();
		 if(StringUtils.equals("includeExternalExpression", ((JAXBElement<?>) typeTransformOpertaionList.get(0)).getName().getLocalPart()))
		 {
			 	ExpressionData expressionData=FilterLogicExternalOperationExpressionUtil.INSTANCE
				 .importExpression(new File(relativePath.toString()), null, false, uiComponent.getComponentName());
				expressionData.getExternalExpressionData().setExternal(true);
				expressionData.getExternalExpressionData().setFilePath(filePath);
				filterLogicDataStructure.setExpressionEditorData(expressionData);
		 }
		 else {
				OperationClassData operationClassData=FilterLogicExternalOperationExpressionUtil.INSTANCE
			    		 .importOperation(new File(relativePath.toString()), null, false, uiComponent.getComponentName());
			    operationClassData.getExternalOperationClassData().setExternal(true);
			    operationClassData.getExternalOperationClassData().setFilePath(filePath);
			    filterLogicDataStructure.setOperationClassData(operationClassData);
				 
		 }
	}


	private List<String> getOperationFileds(TypeTransformOperation typeTransformOperation) {
		List<String> componentOperationFileds=new ArrayList<>();
		if(typeTransformOperation.getInputFields()!=null)
		{
			typeTransformOperation.getInputFields().getField().forEach(inputField->componentOperationFileds.add(inputField.getName()));
		}
		return componentOperationFileds;
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