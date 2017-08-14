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

import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.OperationClassProperty;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.TransformUiConverter;
import hydrograph.ui.graph.model.Container;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeTransformExpression;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.engine.jaxb.operationstypes.Filter;
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

	private OperationClassProperty getOperationClassOrExpression() {
		OperationClassProperty operationClassProperty=null;
		String clazz=null;
		if(filter.getOperationOrExpression()!=null && filter.getOperationOrExpression().size()!=0){
		
		    if(filter.getOperationOrExpression().get(0) instanceof TypeTransformOperation)
		    {	
		    	TypeTransformOperation transformOperation=(TypeTransformOperation) filter.getOperationOrExpression().get(0);
		    	clazz=transformOperation.getClazz();
		    ExpressionEditorData expressionEditorData=new ExpressionEditorData("",uiComponent.getComponentName());
			operationClassProperty=new OperationClassProperty(getOperationClassName(clazz),clazz, ParameterUtil.isParameter(clazz),false,expressionEditorData);
			 operationClassProperty.setNameValuePropertyList(getProperties(transformOperation));
		    }
		    else if(filter.getOperationOrExpression().get(0) instanceof TypeTransformExpression)
		    {
		     TypeTransformExpression typeTransformExpression=(TypeTransformExpression)filter.getOperationOrExpression().get(0);
		     ExpressionEditorData expressionEditorData=getExpressionEditorData(typeTransformExpression);
		     
		     operationClassProperty=new OperationClassProperty(null,null, false,true,expressionEditorData);
		     operationClassProperty.setNameValuePropertyList(getProperties(typeTransformExpression));
		    }
		}
		return operationClassProperty;
	}


	private List<String> getOperationFileds() {
		List<String> componentOperationFileds=new ArrayList<>();;
		
			for(Object object:filter.getOperationOrExpression())
			{
			 if(object instanceof TypeTransformOperation)
			 {
				TypeTransformOperation transformOperation=(TypeTransformOperation) object;
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
