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

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeMapField;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.join.TypeKeyFields;
import hydrograph.engine.jaxb.operationstypes.Join;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.JoinConfigProperty;
import hydrograph.ui.datastructure.property.JoinMappingGrid;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.converter.TransformUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.geometry.Dimension;
import org.slf4j.Logger;

/**
 * 
 * Converts XML to UI object
 * 
 * @author Bitwise
 *
 */
public class JoinComponentUiConverter extends TransformUiConverter {

	private Join join;

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(JoinComponentUiConverter.class);
	private int inPortCounter = 0;
	private int unusedPortCounter = 0;

	public JoinComponentUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new hydrograph.ui.graph.model.components.Join();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {
		join = (Join) typeBaseComponent;
		super.prepareUIXML();
		LOGGER.debug("Fetching Join-Properties for -{}", componentName);

		if(inPortCounter<=2){
			inPortCounter = 2;
		}
		
		propertyMap.put(Constants.UNUSED_AND_INPUT_PORT_COUNT_PROPERTY, String.valueOf(inPortCounter));
		propertyMap.put(Constants.INPUT_PORT_COUNT_PROPERTY, String.valueOf(inPortCounter));
		propertyMap.put(Constants.UNUSED_PORT_COUNT_PROPERTY, String.valueOf(inPortCounter));
		propertyMap.put(Constants.OUTPUT_PORT_COUNT_PROPERTY, String.valueOf(1));
		propertyMap.put(Constants.JOIN_CONFIG_FIELD, getJoinConfigProperty());
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);

		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.JOIN.value());
		uiComponent.setSize(new Dimension(((inPortCounter+1)*33), ((inPortCounter+1)*27) + 16));
	}

	private List<JoinConfigProperty> getJoinConfigProperty() {
		List<JoinConfigProperty> joinConfigPropertyList = null;
		JoinConfigProperty joinConfigProperty = null;
		if (join.getKeys() != null && !join.getKeys().isEmpty()) {
			joinConfigPropertyList = new ArrayList<>();
			for (TypeKeyFields typeKeysFields : join.getKeys()) {
				joinConfigProperty = new JoinConfigProperty();
				joinConfigProperty.setRecordRequired(getRecordRequired(typeKeysFields));
				joinConfigProperty.setJoinKey(getKeyNames(typeKeysFields));
				joinConfigProperty.setPortIndex(typeKeysFields.getInSocketId());
				joinConfigPropertyList.add(joinConfigProperty);
			}
		}
		return joinConfigPropertyList;
	}

	private String getKeyNames(TypeKeyFields typeKeyFields) {
		StringBuilder joinKey = new StringBuilder();
		if (typeKeyFields != null && !typeKeyFields.getField().isEmpty()) {
			for (TypeFieldName typeFieldName : typeKeyFields.getField()) {
				joinKey.append(typeFieldName.getName()).append(",");
			}
		}
		if (joinKey.lastIndexOf(",") != -1)
			joinKey = joinKey.deleteCharAt(joinKey.lastIndexOf(","));
		return joinKey.toString();
	}
	
	
	private Integer getRecordRequired(TypeKeyFields typeKeysFields) {
		int recordRequiredNumber;
		if(typeKeysFields.isRecordRequired())
		{
			recordRequiredNumber=0;
		}
		else
		{
			recordRequiredNumber=1;
		}
		return recordRequiredNumber;
	}

	protected void getInPort(TypeOperationsComponent operationsComponent) {
		LOGGER.debug("Generating InPut Ports for -{}", componentName);
		if (operationsComponent.getInSocket() != null) {
			for (TypeBaseInSocket inSocket : operationsComponent.getInSocket()) {
				uiComponent.engageInputPort(getInputSocketType(inSocket) + inPortCounter);
				currentRepository.getComponentLinkList().add(
						new LinkingData(inSocket.getFromComponentId(), operationsComponent.getId(), inSocket
								.getFromSocketId(), inSocket.getId()));
				inPortCounter++;
			}

			if (inPortCounter > 2) {
			incrementPort();
			}
		}

	}

	private void incrementPort() {
		uiComponent.completeInputPortSettings(inPortCounter);
		uiComponent.unusedPortSettings(inPortCounter);
		
	}

	protected void getOutPort(TypeOperationsComponent operationsComponent) {
		LOGGER.debug("Generating OutPut Ports for -{}", componentName);
		if (operationsComponent.getOutSocket() != null) {
			for (TypeOperationsOutSocket outSocket : operationsComponent.getOutSocket()) {
				
				if(StringUtils.equalsIgnoreCase(Constants.UNUSED_SOCKET_TYPE, outSocket.getType())){
					unusedPortCounter++;
				}
					
				uiComponent.engageOutputPort(outSocket.getId());
				if (outSocket.getPassThroughFieldOrOperationFieldOrExpressionField() != null
						&& !outSocket.getPassThroughFieldOrOperationFieldOrExpressionField().isEmpty()){
					propertyMap.put(Constants.JOIN_MAP_FIELD, getJoinMappingGrid(outSocket));
					createPassThroughAndMappingFieldsForSchemaPropagation(outSocket);
					
				} else if(outSocket.getCopyOfInsocket()!=null && 
				     StringUtils.equalsIgnoreCase(outSocket.getType(), Constants.OUTPUT_SOCKET_TYPE)){
					JoinMappingGrid joinMappingGrid = new JoinMappingGrid();
					joinMappingGrid.setButtonText(Constants.COPY_FROM_INPUT_PORT_PROPERTY + 
												  outSocket.getCopyOfInsocket().getInSocketId());
					joinMappingGrid.setIsSelected(true);
					propertyMap.put(Constants.JOIN_MAP_FIELD,joinMappingGrid);
					copySchemaFromInputPort(outSocket.getCopyOfInsocket().getInSocketId());
				}
			}

		}
		if(unusedPortCounter>inPortCounter){
			inPortCounter=unusedPortCounter;
			incrementPort();
		}
			
	}

	private JoinMappingGrid getJoinMappingGrid(TypeOperationsOutSocket outSocket) {
		String dot_separator = ".";
		LookupMapProperty lookupMapProperty = null;
		JoinMappingGrid joinMappingGrid = new JoinMappingGrid();
		for (Object object : outSocket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
			if ((TypeInputField.class).isAssignableFrom(object.getClass())) {
				TypeInputField inputField=(TypeInputField) object;
				if (StringUtils.isNotBlank(inputField.getName()) && StringUtils.isNotBlank(inputField.getInSocketId())) {
					lookupMapProperty = new LookupMapProperty();
					lookupMapProperty.setOutput_Field(inputField.getName());
					lookupMapProperty
							.setSource_Field(inputField.getInSocketId() + dot_separator + inputField.getName());
					joinMappingGrid.getLookupMapProperties().add(lookupMapProperty);
				}
			}
			if ((TypeMapField.class).isAssignableFrom(object.getClass())) {
				TypeMapField mapField = (TypeMapField) object;
				if (StringUtils.isNotBlank(mapField.getName()) && StringUtils.isNotBlank(mapField.getSourceName())) {
					lookupMapProperty = new LookupMapProperty();
					lookupMapProperty.setOutput_Field(mapField.getName());
					lookupMapProperty.setSource_Field(mapField.getInSocketId() + dot_separator	+ mapField.getSourceName());
					joinMappingGrid.getLookupMapProperties().add(lookupMapProperty);
				}
			}
		}
		return joinMappingGrid;
	}
}
