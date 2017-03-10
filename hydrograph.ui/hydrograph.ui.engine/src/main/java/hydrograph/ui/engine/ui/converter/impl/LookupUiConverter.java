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

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.geometry.Dimension;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeMapField;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.lookup.TypeKeyFields;
import hydrograph.engine.jaxb.operationstypes.Lookup;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.LookupConfigProperty;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.datastructure.property.MatchValueProperty;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.converter.TransformUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.PortTypeEnum;
import hydrograph.ui.logging.factory.LogFactory;

public class LookupUiConverter extends TransformUiConverter {


	private Lookup lookup;
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(LookupUiConverter.class);
	private static final String lookupLabel = "lkp";
	private static final String driverLabel = "drv";
	private static final String IN0_PORT = "in0";
	private static final String IN1_PORT = "in1";
	private static final int inPortCounter = 2;
	
	
	public LookupUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new hydrograph.ui.graph.model.components.Lookup();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Lookup-Properties for -{}", componentName);
		lookup = (Lookup) typeBaseComponent;
		
		getPortLabels();
			
		propertyMap.put(Constants.MATCH_PROPERTY_WIDGET, getMatch());
		
		LOGGER.info("LOOKUP_CONFIG_FIELD::{}",getLookupConfigProperty());
		propertyMap.put(Constants.LOOKUP_CONFIG_FIELD, getLookupConfigProperty());

		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);

		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.LOOKUP.value());
		uiComponent.setSize(new Dimension(((inPortCounter+1)*33), ((inPortCounter+1)*27) + 16));
		
	}
	
	protected void getInPort(TypeOperationsComponent operationsComponent) {
		LOGGER.debug("Generating InPut Ports for -{}", componentName);
		if (operationsComponent.getInSocket() != null) {
			for (TypeBaseInSocket inSocket : operationsComponent.getInSocket()) {
				uiComponent.engageInputPort(inSocket.getId());
				currentRepository.getComponentLinkList().add(
						new LinkingData(inSocket.getFromComponentId(),
								operationsComponent.getId(), inSocket
										.getFromSocketId(), inSocket.getId()));
				if(inSocket.getType().equalsIgnoreCase(PortTypeEnum.LOOKUP.value())){
					uiComponent.getPorts().get(inSocket.getId()).setPortType(PortTypeEnum.LOOKUP);
				}else{
					uiComponent.getPorts().get(inSocket.getId()).setPortType(PortTypeEnum.DRIVER);
				}
			}
		}
	}
	

	private void getPortLabels(){
		LOGGER.debug("Generating Port labels for -{}", componentName);
		if (lookup.getInSocket() != null) {
			for (TypeBaseInSocket inSocket : lookup.getInSocket()) {
				if(StringUtils.equalsIgnoreCase(inSocket.getType(), PortTypeEnum.LOOKUP.value()))
					uiComponent.getPorts().get(inSocket.getId()).setLabelOfPort(lookupLabel);
				else if(StringUtils.equalsIgnoreCase(inSocket.getType(), PortTypeEnum.DRIVER.value()))
					uiComponent.getPorts().get(inSocket.getId()).setLabelOfPort(driverLabel);
			}
		}
	}
	
	private MatchValueProperty getMatch() {
		LOGGER.debug("Generating Match for -{}", componentName);
		MatchValueProperty matchValue =  new MatchValueProperty();
		matchValue.setMatchValue(lookup.getMatch().getValue().toString());
		matchValue.setRadioButtonSelected(true);
		return matchValue;
		
	}

	private LookupConfigProperty getLookupConfigProperty() {
		LookupConfigProperty lookupConfigProperty = null;
		List<TypeKeyFields> typeKeyFieldsList = lookup.getKeys();
		
		if (typeKeyFieldsList != null && !typeKeyFieldsList.isEmpty()) {
			lookupConfigProperty = new LookupConfigProperty();
			for (TypeKeyFields typeKeyFields : typeKeyFieldsList) {
				if(StringUtils.equalsIgnoreCase(typeKeyFields.getInSocketId(), IN0_PORT)){
					for (TypeBaseInSocket inSocket : lookup.getInSocket()) {
						if(StringUtils.equalsIgnoreCase(inSocket.getId(), IN0_PORT)){
							if(StringUtils.equalsIgnoreCase(inSocket.getType(), PortTypeEnum.LOOKUP.value())){
								lookupConfigProperty.setLookupKey(getKeyNames(typeKeyFields));
								lookupConfigProperty.setSelected(true);
							}else if(StringUtils.equalsIgnoreCase(inSocket.getType(), PortTypeEnum.DRIVER.value())){
								lookupConfigProperty.setDriverKey(getKeyNames(typeKeyFields));
							}	
						}
					}
					
				}else if (StringUtils.equalsIgnoreCase(typeKeyFields.getInSocketId(), IN1_PORT)){
					for (TypeBaseInSocket inSocket : lookup.getInSocket()) {
						if(StringUtils.equalsIgnoreCase(inSocket.getId(), IN1_PORT)){
							if(StringUtils.equalsIgnoreCase(inSocket.getType(), PortTypeEnum.LOOKUP.value())){
								lookupConfigProperty.setLookupKey(getKeyNames(typeKeyFields));
								lookupConfigProperty.setSelected(false);
							}else if(StringUtils.equalsIgnoreCase(inSocket.getType(), PortTypeEnum.DRIVER.value())){
								lookupConfigProperty.setDriverKey(getKeyNames(typeKeyFields));
							}	
						}
					}
				}		
			}
		}
		
		return lookupConfigProperty;
	}

	private String getKeyNames(TypeKeyFields typeKeyFields) {
		StringBuilder lookupKey = new StringBuilder("");
		if (typeKeyFields != null && !typeKeyFields.getField().isEmpty()) {
			for (TypeFieldName typeFieldName : typeKeyFields.getField()) {
				lookupKey.append(typeFieldName.getName()).append(",");
			}
		}
		if( lookupKey.lastIndexOf(",")!=-1)
			lookupKey=lookupKey.deleteCharAt(lookupKey.lastIndexOf(","));
		return lookupKey.toString();
	}
	
protected void getOutPort(TypeOperationsComponent operationsComponent) {
		LOGGER.debug("Generating OutPut Ports for -{}", componentName);
		if (operationsComponent.getOutSocket() != null) {
			for (TypeOperationsOutSocket outSocket : operationsComponent.getOutSocket()) {
				uiComponent.engageOutputPort(outSocket.getId());
				if (outSocket.getPassThroughFieldOrOperationFieldOrExpressionField() != null
						&& !outSocket.getPassThroughFieldOrOperationFieldOrExpressionField().isEmpty()){
					propertyMap.put(Constants.LOOKUP_MAP_FIELD, getLookupMappingGrid(outSocket));
					createPassThroughAndMappingFieldsForSchemaPropagation(outSocket);
					}
			}
		}			
	}

	private LookupMappingGrid getLookupMappingGrid(TypeOperationsOutSocket outSocket) {
		String dot_separator = ".";
		LookupMapProperty lookupMapProperty = null;
		LookupMappingGrid lookupMappingGrid = new LookupMappingGrid();
		for (Object object : outSocket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
			if ((TypeInputField.class).isAssignableFrom(object.getClass())) {
				TypeInputField inputField=(TypeInputField) object;
				if (StringUtils.isNotBlank(inputField.getName()) && StringUtils.isNotBlank(inputField.getInSocketId())) {
					lookupMapProperty = new LookupMapProperty();
					lookupMapProperty.setOutput_Field(inputField.getName());
					lookupMapProperty
							.setSource_Field(inputField.getInSocketId() + dot_separator + inputField.getName());
					lookupMappingGrid.getLookupMapProperties().add(lookupMapProperty);
				}
			}
			if ((TypeMapField.class).isAssignableFrom(object.getClass())) {
				TypeMapField mapField = (TypeMapField) object;
				if (StringUtils.isNotBlank(mapField.getName()) && StringUtils.isNotBlank(mapField.getSourceName())) {
					lookupMapProperty = new LookupMapProperty();
					lookupMapProperty.setOutput_Field(mapField.getName());
					lookupMapProperty.setSource_Field(mapField.getInSocketId() + dot_separator	+ mapField.getSourceName());
					lookupMappingGrid.getLookupMapProperties().add(lookupMapProperty);
				} 
			}
		}
		return lookupMappingGrid;
	}
	
}
