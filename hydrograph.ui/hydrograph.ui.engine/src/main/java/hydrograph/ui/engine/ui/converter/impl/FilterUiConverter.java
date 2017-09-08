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

import javax.xml.bind.JAXBElement;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeTransformExpression;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.engine.jaxb.operationstypes.Filter;
import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.common.datastructure.filter.OperationClassData;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.TransformUiConverter;
import hydrograph.ui.graph.model.Container;
/**
 * The class FilterUiConverter
 * 
 * @author Bitwise
 * 
 */
public class FilterUiConverter extends TransformUiConverter{

	private Filter filter;
	

	public FilterUiConverter(TypeBaseComponent typeBaseComponent,Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new hydrograph.ui.graph.model.components.Filter();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();

		filter = (Filter) typeBaseComponent;
		
		propertyMap.put(PropertyNameConstants.OPERATION_CLASS.value(),getOperationClassOrExpression());
		propertyMap.put(PropertyNameConstants.OPERATION_FILEDS.value(), getOperationFileds());
		
		
		
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.FILTER.value());
		
	}

	private FilterLogicDataStructure getOperationClassOrExpression() {
		FilterLogicDataStructure filterLogicDataStructure =new FilterLogicDataStructure(uiComponent.getComponentName());
		String clazz=null;
		if(filter.getOperationOrExpressionOrIncludeExternalOperation()!=null && filter.getOperationOrExpressionOrIncludeExternalOperation().size()!=0){
		
		    if(filter.getOperationOrExpressionOrIncludeExternalOperation().get(0).getValue() instanceof TypeTransformOperation)
		    {	
		    	TypeTransformOperation transformOperation=(TypeTransformOperation) filter.getOperationOrExpressionOrIncludeExternalOperation().get(0).getValue();
		    	clazz=transformOperation.getClazz();
			 filterLogicDataStructure.setOperation(true);
			 OperationClassData operationClassData = new OperationClassData();
			 operationClassData.setQualifiedOperationClassName(getOperationClassName(clazz));
			 filterLogicDataStructure.setOperationClassData(operationClassData);
			 
		    }
		    else if(filter.getOperationOrExpressionOrIncludeExternalOperation().get(0).getValue() instanceof TypeTransformExpression)
		    {
		     TypeTransformExpression typeTransformExpression=(TypeTransformExpression)filter.getOperationOrExpressionOrIncludeExternalOperation().get(0).getValue();
		     ExpressionEditorData expressionEditorData=getExpressionEditorData(typeTransformExpression);
		     
		     ExpressionData expressionData = new ExpressionData(uiComponent.getComponentName());
			 expressionData.setExpressionEditorData(expressionEditorData);
			 for(TypeInputField inputField : typeTransformExpression.getInputFields().getField()){
				 expressionData.getInputFields().add( inputField.getName());
			 }
			 filterLogicDataStructure.setExpressionEditorData(expressionData);
		    }
		}
		return filterLogicDataStructure;
	}


	private List<String> getOperationFileds() {
		List<String> componentOperationFileds=new ArrayList<>();;
		
			for(Object object:filter.getOperationOrExpressionOrIncludeExternalOperation())
			{
			 if(((JAXBElement)object).getValue() instanceof TypeTransformOperation)
			 {
				TypeTransformOperation transformOperation=(TypeTransformOperation) ((JAXBElement)object).getValue();
				if(transformOperation.getInputFields()!=null)
				{
					
						for(TypeInputField inputFileds:transformOperation.getInputFields().getField())
						{
							componentOperationFileds.add(inputFileds.getName());
				        }
			     }
			  }
			}
			return componentOperationFileds;
	}
	
}
