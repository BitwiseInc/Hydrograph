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


package hydrograph.ui.engine.converter.impl;

import hydrograph.engine.jaxb.commontypes.MatchValue;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.engine.jaxb.lookup.TypeKeyFields;
import hydrograph.engine.jaxb.operationstypes.Lookup;
import hydrograph.engine.jaxb.operationstypes.Lookup.Match;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.LookupConfigProperty;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.datastructure.property.MatchValueProperty;
import hydrograph.ui.engine.converter.TransformConverter;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;


/**
 * Converter implementation for Lookup component
 * @author Bitwise 
 */

public class LookupConverter extends TransformConverter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(FilterConverter.class);
	private LookupMappingGrid lookupPropertyGrid;

	public LookupConverter(Component component) {
		super(component);
		this.baseComponent = new Lookup();
		this.component = component;
		this.properties = component.getProperties();
		lookupPropertyGrid = (LookupMappingGrid) properties.get(Constants.LOOKUP_MAP_FIELD);
	}

	@Override
	public void prepareForXML() {
		super.prepareForXML();
		Lookup lookup = (Lookup) baseComponent;
		if (properties.get(Constants.LOOKUP_CONFIG_FIELD) != null) {
			lookup.getKeys().addAll(getLookupConfigKeys());
		}
		lookup.setMatch(getMatchValueFromUi());
	}

	private Match getMatchValueFromUi() {
		Match match = new Match();
		MatchValueProperty matchValueProperty =  (MatchValueProperty) properties.get(Constants.MATCH_PROPERTY_WIDGET);

		if(matchValueProperty != null){
			if(Constants.LAST.equalsIgnoreCase(matchValueProperty.getMatchValue())){
				match.setValue(MatchValue.LAST);
				return match;
			}else if(Constants.ALL.equalsIgnoreCase(matchValueProperty.getMatchValue())){
				match.setValue(MatchValue.ALL);
				return match;
			}else{
				match.setValue(MatchValue.FIRST);
				return match;
			}
		}
		return match;
	}

	private List<TypeKeyFields> getLookupConfigKeys() {
		List<TypeKeyFields> typeKeyFieldsList = null;
		TypeKeyFields typeKeyField = null;
		LookupConfigProperty keyFields = (LookupConfigProperty) properties.get(Constants.LOOKUP_CONFIG_FIELD);
		if (keyFields != null) {
			typeKeyFieldsList = new ArrayList<>();

			if (keyFields.getDriverKey() != null) {
				typeKeyField = new TypeKeyFields();
				typeKeyField.setInSocketId("in0");
				List<TypeFieldName> typeKeyFields=getTypeFieldName("in0", keyFields.getDriverKey());
				if(typeKeyFields!=null && !typeKeyFields.isEmpty()){
					typeKeyField.getField().addAll(typeKeyFields);
					typeKeyFieldsList.add(typeKeyField);
				}
			}
			if (keyFields.getLookupKey() != null) {
				typeKeyField = new TypeKeyFields();
				typeKeyField.setInSocketId("in1");
				List<TypeFieldName> typeKeyFields=getTypeFieldName("in1", keyFields.getLookupKey());
				if(typeKeyFields!=null && !typeKeyFields.isEmpty()){
					typeKeyField.getField().addAll(typeKeyFields);
					typeKeyFieldsList.add(typeKeyField);}
			}

		}
		return typeKeyFieldsList;
	}

	private List<TypeFieldName> getTypeFieldName(String socketID, String keyData) {
		List<TypeFieldName> typeFieldNameList = null;
		TypeFieldName typeFieldName = null;
		if (keyData != null) {
			typeFieldNameList = new ArrayList<>();
			String keyList[] = keyData.split(",");
			if(keyList.length==0 || (keyList.length==1 && StringUtils.isBlank(keyList[0])))
				return null;
			if (!converterHelper.hasAllStringsInArrayAsParams(keyList)) {
				for (String key : keyList) {
					if (!ParameterUtil.isParameter(key)) {
						typeFieldName = new TypeFieldName();
						typeFieldName.setName(key);
						typeFieldNameList.add(typeFieldName);
					} else {
						converterHelper.addParamTag(this.ID, key, 
								ComponentXpathConstants.LOOKUP_KEYS.value().replace("$inSocketId", socketID), false);
					}
				}
			}else{
				StringBuffer parameterFieldNames=new StringBuffer();
				TypeFieldName field = new TypeFieldName();
				field.setName("");
				typeFieldNameList.add(field);
				for (String fieldName : keyList){ 
					parameterFieldNames.append(fieldName+ " ");
				}
				converterHelper.addParamTag(this.ID, parameterFieldNames.toString(), 
					ComponentXpathConstants.LOOKUP_KEYS.value().replace("$inSocketId", socketID),true);
				
			}

		}

		return typeFieldNameList;
	}

	@Override
	protected List<TypeOperationsOutSocket> getOutSocket() {
		logger.debug("Generating TypeStraightPullOutSocket data for : {}", properties.get(Constants.PARAM_NAME));
		TypeBaseInSocket inSocketsList = new TypeBaseInSocket();
		Object obj = properties.get(Constants.LOOKUP_MAP_FIELD);

		List<TypeOperationsOutSocket> outSockectList = new ArrayList<TypeOperationsOutSocket>();
		for (Link link : component.getSourceConnections()) {
			TypeOperationsOutSocket outSocket = new TypeOperationsOutSocket();
			TypeOutSocketAsInSocket outSocketAsInsocket = new TypeOutSocketAsInSocket();

			outSocketAsInsocket.setInSocketId(link.getTarget().getPort(link.getTargetTerminal()).getTerminal());
			outSocketAsInsocket.getOtherAttributes();
			// outSocket.setCopyOfInsocket(outSocketAsInsocket);
			outSocket.setId(link.getSourceTerminal());
			outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
			outSocket.getOtherAttributes();
			outSockectList.add(outSocket);
			if (properties.get(Constants.LOOKUP_MAP_FIELD) != null)
				outSocket.getPassThroughFieldOrOperationFieldOrExpressionField().addAll(
						converterHelper.getLookuporJoinOutputMaping(lookupPropertyGrid));

		}
		return outSockectList;
	}

	@Override
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

	/*private void setInputProperty(Lookup lookup) {
		List<TypeBaseInSocket> inputField = new ArrayList<>();
		Map<String, String> mapFields = (TreeMap<String, String>) properties.get(Constants.LOOKUP_MAP_FIELD);
		if (mapFields != null) {
			TypeInputField typeInputField = new TypeInputField();
			TypeMapField mapField = new TypeMapField();

			for (Entry<String, String> entry : mapFields.entrySet()) {
				String[] value = entry.getKey().split(Pattern.quote("."));
				if (entry.getKey().equalsIgnoreCase(entry.getValue())) {

					typeInputField.setName(entry.getKey());
					typeInputField.setInSocketId(value[0]);

				} else {
					mapField.setSourceName(entry.getKey());
					mapField.setName(entry.getValue());
					mapField.setInSocketId(value[0]);
				}
			}
		}

	}*/

	@Override
	protected List<JAXBElement<?>> getOperations() {
		return null;
	}

}
