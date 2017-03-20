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

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeSortOrder;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;
import hydrograph.engine.jaxb.sort.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.sort.TypePrimaryKeyFieldsAttributes;
import hydrograph.engine.jaxb.straightpulltypes.Sort;

/**
 * Converter to convert sort component into engine specific sort object
 *
 *@author BITWISE
 */
public class SortConverter extends StraightPullConverter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(SortConverter.class);

	public SortConverter(Component component) {
		super(component);
		this.baseComponent = new Sort();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for : {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		Sort sort = (Sort) baseComponent;
		sort.setPrimaryKeys(getPrimaryKeys());
	}
	
	private TypePrimaryKeyFields getPrimaryKeys() {

		Map<String, String> primaryKeyRow = (Map<String, String>) properties.get(Constants.PARAM_PRIMARY_COLUMN_KEYS);

		TypePrimaryKeyFields primaryKeyFields = null;
		if (primaryKeyRow != null &&  !primaryKeyRow.isEmpty()) {
			primaryKeyFields = new TypePrimaryKeyFields();
			List<TypePrimaryKeyFieldsAttributes> fieldNameList = primaryKeyFields.getField();
			if (!converterHelper.hasAllKeysAsParams(primaryKeyRow)) {
				for (Map.Entry<String, String> primaryKeyRowEntry : primaryKeyRow.entrySet()) {
					if(!ParameterUtil.isParameter(primaryKeyRowEntry.getKey())){
						TypePrimaryKeyFieldsAttributes field = new TypePrimaryKeyFieldsAttributes();
						field.setName(primaryKeyRowEntry.getKey());
						field.setOrder(TypeSortOrder.fromValue(primaryKeyRowEntry.getValue().toLowerCase(Locale.ENGLISH)));
						fieldNameList.add(field);
					}else{
						converterHelper.addParamTag(this.ID, primaryKeyRowEntry.getKey(), 
								ComponentXpathConstants.STRAIGHTPULL_PRIMARY_KEYS.value(), false);
					}
				}
			}else{
				StringBuffer parameterFieldNames = new StringBuffer();
				TypePrimaryKeyFieldsAttributes fieldsAttributes = new TypePrimaryKeyFieldsAttributes();
				fieldsAttributes.setName("");
				fieldNameList.add(fieldsAttributes);
				for (Entry<String, String> secondaryKeyRowEntry : primaryKeyRow.entrySet())
					parameterFieldNames.append(secondaryKeyRowEntry.getKey() + " ");
				converterHelper.addParamTag(this.ID, parameterFieldNames.toString(),
						ComponentXpathConstants.STRAIGHTPULL_PRIMARY_KEYS.value(), true);
			}

		}
		return primaryKeyFields;
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