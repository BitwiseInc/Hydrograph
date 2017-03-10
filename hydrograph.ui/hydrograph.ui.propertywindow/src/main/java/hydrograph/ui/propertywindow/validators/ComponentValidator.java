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

package hydrograph.ui.propertywindow.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.datastructures.tooltip.PropertyToolTipInformation;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.model.Component;

/**
 * 
 * Component state validator
 * 
 * @author Bitwise
 *
 */
public class ComponentValidator {
	public static ComponentValidator INSTANCE = new ComponentValidator();
	
	private Map<String,List<IComponentValidator>> componentValidators;
	
	private static final String ISSUE_PROPERTY_NAME="Other Issues";
	
	private static final String SHOW_TOOLTIP="TRUE";
	
	private static final String HIDE_TOOLTIP="FALSE";
	
	private static final String TOOLTIP_DATATYPE="TEXT";
	
	private ComponentValidator(){
		registerComponentValidators();
	}
	
	private void registerComponentValidators(){
		componentValidators = new HashMap<>();
		componentValidators.put(Constants.JOIN, new ArrayList<IComponentValidator>());
		componentValidators.get(Constants.JOIN).add(new ComponentSchemaMapValidator());
		
		componentValidators.put(Constants.TRANSFORM, new ArrayList<IComponentValidator>());
		componentValidators.get(Constants.TRANSFORM).add(new ComponentSchemaMapValidator());
		
		componentValidators.put(Constants.AGGREGATE, new ArrayList<IComponentValidator>());
		componentValidators.get(Constants.AGGREGATE).add(new ComponentSchemaMapValidator());
		
		componentValidators.put(Constants.GROUP_COMBINE, new ArrayList<IComponentValidator>());
		componentValidators.get(Constants.GROUP_COMBINE).add(new ComponentSchemaMapValidator());
		
		componentValidators.put(Constants.CUMULATE, new ArrayList<IComponentValidator>());
		componentValidators.get(Constants.CUMULATE).add(new ComponentSchemaMapValidator());
		
		componentValidators.put(Constants.NORMALIZE, new ArrayList<IComponentValidator>());
		componentValidators.get(Constants.NORMALIZE).add(new ComponentSchemaMapValidator());
		
		componentValidators.put(Constants.LOOKUP, new ArrayList<IComponentValidator>());
		componentValidators.get(Constants.LOOKUP).add(new ComponentSchemaMapValidator());
	}
	
	/**
	 * Validates given component state
	 * 
	 * @param {@link Component}
	 * @return true - if component has valid state
	 */
	public boolean validate(Component component){
		PropertyToolTipInformation propertyToolTipInformation= new PropertyToolTipInformation(ISSUE_PROPERTY_NAME, HIDE_TOOLTIP, TOOLTIP_DATATYPE);
		boolean validationStatus=true;
		String errorMessages="";
		if(componentValidators!=null && componentValidators.containsKey(component.getType().toUpperCase())){
			for(IComponentValidator componentValidator: componentValidators.get(component.getType().toUpperCase())){
				
				String errorMessage = componentValidator.validateComponent(component);
				if(errorMessage!=null){
					errorMessages = errorMessages + errorMessage + "\n";
				}
			}
			
			errorMessages=StringUtils.substringBeforeLast(errorMessages, "\n");
			
			if(!StringUtils.isEmpty(errorMessages)){
				propertyToolTipInformation= new PropertyToolTipInformation(ISSUE_PROPERTY_NAME, SHOW_TOOLTIP, TOOLTIP_DATATYPE);
				propertyToolTipInformation.setPropertyValue(errorMessages);
				validationStatus = false;
			}
			
			component.getTooltipInformation().put(ISSUE_PROPERTY_NAME,propertyToolTipInformation );
		}
		
		return validationStatus;
	}
}
