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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.StraightPullConverter;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.KeepValue;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeSortOrder;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;
import hydrograph.engine.jaxb.removedups.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.removedups.TypeSecondaryKeyFields;
import hydrograph.engine.jaxb.removedups.TypeSecondayKeyFieldsAttributes;
import hydrograph.engine.jaxb.straightpulltypes.RemoveDups;
import hydrograph.engine.jaxb.straightpulltypes.RemoveDups.Keep;

/**
 * @author Bitwise Converter implementation for RemoveDups component
 */

public class RemoveDupsConverter extends StraightPullConverter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(RemoveDupsConverter.class);

	public RemoveDupsConverter(Component component) {
		super(component);
		this.baseComponent = new RemoveDups();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for : {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		RemoveDups dedup = (RemoveDups) baseComponent;
		dedup.setKeep(getKeep());
		dedup.setPrimaryKeys(getPrimaryKeys());
		dedup.setSecondaryKeys(getSecondaryKeys());
	}

	private TypePrimaryKeyFields getPrimaryKeys() {

		List<String> fieldValueSet = (List<String>) properties.get(PropertyNameConstants.DEDUP_FILEDS.value());

		TypePrimaryKeyFields typePrimaryKeyFields = null;
		if (fieldValueSet != null) {
			typePrimaryKeyFields = new TypePrimaryKeyFields();
			List<TypeFieldName> fieldNameList = typePrimaryKeyFields.getField();
			if (!converterHelper.hasAllStringsInListAsParams(fieldValueSet)) {
			for (String value : fieldValueSet) {
				if(!ParameterUtil.isParameter(value)){
					TypeFieldName field = new TypeFieldName();
					field.setName(value);
					fieldNameList.add(field);
				}else{
					converterHelper.addParamTag(this.ID, value, ComponentXpathConstants.STRAIGHTPULL_PRIMARY_KEYS.value(), false);
				}
			}
			}else{
				StringBuffer parameterFieldNames=new StringBuffer();
				TypeFieldName typeFieldName = new TypeFieldName();
				typeFieldName.setName("");
				fieldNameList.add(typeFieldName);
				for (String fieldName : fieldValueSet){ 
					parameterFieldNames.append(fieldName+ " ");
				}
				converterHelper.addParamTag(this.ID, parameterFieldNames.toString(), 
						ComponentXpathConstants.STRAIGHTPULL_PRIMARY_KEYS.value(),true);
				
			}

		}
		return typePrimaryKeyFields;
	}

	private TypeSecondaryKeyFields getSecondaryKeys() {

		Map<String, String> secondaryKeyRow = (Map<String, String>) properties.get(PropertyNameConstants.SECONDARY_COLUMN_KEYS.value());
		
		TypeSecondaryKeyFields typeSecondaryKeyFields = null;
		if (secondaryKeyRow != null && !secondaryKeyRow.isEmpty()) {
			typeSecondaryKeyFields = new TypeSecondaryKeyFields();
			List<TypeSecondayKeyFieldsAttributes> fieldNameList = typeSecondaryKeyFields.getField();
			if (!converterHelper.hasAllKeysAsParams(secondaryKeyRow)) {
				for (Map.Entry<String, String> secondaryKeyRowEntry : secondaryKeyRow.entrySet()) {
					if(!ParameterUtil.isParameter(secondaryKeyRowEntry.getKey())){
						TypeSecondayKeyFieldsAttributes field = new TypeSecondayKeyFieldsAttributes();
						field.setName(secondaryKeyRowEntry.getKey());
						field.setOrder(TypeSortOrder.fromValue(secondaryKeyRowEntry.getValue().toLowerCase(Locale.ENGLISH)));
						fieldNameList.add(field);
					}else{
						converterHelper.addParamTag(this.ID, secondaryKeyRowEntry.getKey(), 
								ComponentXpathConstants.STRAIGHTPULL_SECONDARY_KEYS.value(), false);
					}
				}
			}else{
				StringBuffer parameterFieldNames = new StringBuffer();
				TypeSecondayKeyFieldsAttributes fieldsAttributes = new TypeSecondayKeyFieldsAttributes();
				fieldsAttributes.setName("");
				fieldNameList.add(fieldsAttributes);
				for (Entry<String, String> secondaryKeyRowEntry : secondaryKeyRow.entrySet())
					parameterFieldNames.append(secondaryKeyRowEntry.getKey() + " ");
				converterHelper.addParamTag(this.ID, parameterFieldNames.toString(),
						ComponentXpathConstants.STRAIGHTPULL_SECONDARY_KEYS.value(), true);
			}
		}
		return typeSecondaryKeyFields;
	}

	private Keep getKeep() {
		logger.debug("Generating Retention Logic for ::{}", componentName);
		String keepValue =(String) properties.get(PropertyNameConstants.RETENTION_LOGIC_KEEP.value());
		Keep keep = new Keep();
		if(StringUtils.isNotBlank(keepValue)){
			keep.setValue(KeepValue.fromValue(StringUtils.lowerCase(keepValue)));
		}
		else{
			keep.setValue(KeepValue.fromValue(StringUtils.lowerCase(Constants.FIRST)));
		}
		return keep;
	}

	@Override
	protected List<TypeStraightPullOutSocket> getOutSocket() {
		logger.debug("Generating TypeStraightPullOutSocket data for : {}", properties.get(Constants.PARAM_NAME));
		List<TypeStraightPullOutSocket> outSockectList = new ArrayList<TypeStraightPullOutSocket>();

		for (Link link : component.getSourceConnections()) {
			TypeStraightPullOutSocket outSocket = new TypeStraightPullOutSocket();
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

}
