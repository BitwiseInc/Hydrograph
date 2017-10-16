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

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.aggregate.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.aggregate.TypeSecondaryKeyFields;
import hydrograph.engine.jaxb.aggregate.TypeSecondayKeyFieldsAttributes;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeSortOrder;
import hydrograph.engine.jaxb.operationstypes.Aggregate;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.engine.converter.TransformConverter;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Aggregate converter
 * 
 * @author Bitwise 
 */

public class AggregateConverter extends TransformConverter {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(AggregateConverter.class);
	private TransformMapping transformMapping;
	private List<BasicSchemaGridRow> schemaGridRows;

	public AggregateConverter(Component component) {
		super(component);
		this.baseComponent = new Aggregate();
		this.component = component;
		this.properties = component.getProperties();
		transformMapping = (TransformMapping) properties.get(Constants.PARAM_OPERATION);
		initSchemaGridRows();
	}


	private void initSchemaGridRows() {
		schemaGridRows = new LinkedList<>();
		Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) properties
				.get(Constants.SCHEMA_TO_PROPAGATE);
		if (schemaMap != null && schemaMap.get(Constants.FIXED_OUTSOCKET_ID) != null) {
			ComponentsOutputSchema componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);
			List<GridRow> gridRows = componentsOutputSchema.getSchemaGridOutputFields(null);

			for (GridRow row : gridRows) {
				schemaGridRows.add((BasicSchemaGridRow) row.copy());
			}
		}
	}


	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for :{}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();

		Aggregate aggregate = (Aggregate) baseComponent;
		aggregate.getOperationOrExpressionOrIncludeExternalOperation().addAll(getOperations());
		setPrimaryKeys(aggregate);
		setSecondaryKeys(aggregate);
	}

	@Override
	protected List<JAXBElement<?>> getOperations() {
		return converterHelper.getOperationsOrExpression(transformMapping, schemaGridRows);
	}

	@Override
	protected List<TypeOperationsOutSocket> getOutSocket() {
		return converterHelper.getOutSocket(transformMapping, schemaGridRows);
	}

	@Override
	public List<TypeBaseInSocket> getInSocket() {
		return converterHelper.getInSocket();
	}

	private void setPrimaryKeys(Aggregate aggregate) {
		logger.debug("Generating XML for :{}", properties.get(Constants.PROPERTY_COLUMN_NAME));
		List<String> columnNameProperties = (List<String>) component.getProperties()
				.get(Constants.PROPERTY_COLUMN_NAME);
		TypePrimaryKeyFields primaryKeyFields = new TypePrimaryKeyFields();
		if (columnNameProperties != null ) {
			aggregate.setPrimaryKeys(primaryKeyFields);
			List<TypeFieldName> field = primaryKeyFields.getField();
			if (!converterHelper.hasAllStringsInListAsParams(columnNameProperties)) {
				
					
					for (String columnNameProperty : columnNameProperties) {
					if (!ParameterUtil.isParameter(columnNameProperty)) {
						TypeFieldName fieldName = new TypeFieldName();
						fieldName.setName(columnNameProperty);
						field.add(fieldName);
					} else {
						converterHelper.addParamTag(this.ID, columnNameProperty,
								ComponentXpathConstants.OPERATIONS_PRIMARY_KEYS.value(), false);
					}
					}
				
				
			}
			else if(columnNameProperties.isEmpty())
			{
				primaryKeyFields.setNone("");
			}
			else {
				StringBuffer parameterFieldNames = new StringBuffer();
				TypeFieldName fieldName = new TypeFieldName();
				fieldName.setName("");
				field.add(fieldName);
				for (String fName : columnNameProperties)
					parameterFieldNames.append(fName + " ");
				converterHelper.addParamTag(this.ID, parameterFieldNames.toString(),
						ComponentXpathConstants.OPERATIONS_PRIMARY_KEYS.value(), true);
			}
		}else{
			aggregate.setPrimaryKeys(primaryKeyFields);
			primaryKeyFields.setNone("");
		}
	}


	private void setSecondaryKeys(Aggregate aggregate) {
		logger.debug("Generating XML for :{}", properties.get(Constants.PROPERTY_SECONDARY_COLUMN_KEYS));
		Map<String, String> secondaryKeyRow = (Map<String, String>) component.getProperties().get(
				Constants.PROPERTY_SECONDARY_COLUMN_KEYS);
		if (secondaryKeyRow != null && !secondaryKeyRow.isEmpty()) {
			TypeSecondaryKeyFields secondaryKeyFields = new TypeSecondaryKeyFields();
			aggregate.setSecondaryKeys(secondaryKeyFields);
			List<TypeSecondayKeyFieldsAttributes> field = secondaryKeyFields.getField();
			if (!converterHelper.hasAllKeysAsParams(secondaryKeyRow)) {

				for (Entry<String, String> secondaryKeyRowEntry : secondaryKeyRow.entrySet()) {

					if (!ParameterUtil.isParameter(secondaryKeyRowEntry.getKey())) {
						TypeSecondayKeyFieldsAttributes fieldsAttributes = new TypeSecondayKeyFieldsAttributes();
						fieldsAttributes.setName(secondaryKeyRowEntry.getKey());
						TypeSortOrder order = TypeSortOrder.fromValue(secondaryKeyRowEntry.getValue().toLowerCase(Locale.ENGLISH));
						fieldsAttributes.setOrder(order);
						field.add(fieldsAttributes);
					} else {
						converterHelper.addParamTag(this.ID, secondaryKeyRowEntry.getKey(),
								ComponentXpathConstants.OPERATIONS_SECONDARY_KEYS.value(), false);
					}
				}
			} else {
				StringBuffer parameterFieldNames = new StringBuffer();
				TypeSecondayKeyFieldsAttributes fieldsAttributes = new TypeSecondayKeyFieldsAttributes();
				fieldsAttributes.setName("");
				field.add(fieldsAttributes);
				for (Entry<String, String> secondaryKeyRowEntry : secondaryKeyRow.entrySet())
					parameterFieldNames.append(secondaryKeyRowEntry.getKey() + " ");
				converterHelper.addParamTag(this.ID, parameterFieldNames.toString(),
						ComponentXpathConstants.OPERATIONS_SECONDARY_KEYS.value(), true);
			}
		}
	}
}
