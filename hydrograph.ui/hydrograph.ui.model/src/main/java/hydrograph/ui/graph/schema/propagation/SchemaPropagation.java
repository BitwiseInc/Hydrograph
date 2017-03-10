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

 
package hydrograph.ui.graph.schema.propagation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * This class is used to propagate schema.
 * 
 * @author Bitwise
 * 
 */
public class SchemaPropagation {
	public static final SchemaPropagation INSTANCE = new SchemaPropagation();
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(SchemaPropagation.class);
	private ComponentsOutputSchema componentsOutputSchema;
	private List<Link> componentsLinkList = new ArrayList<>();
	private List<Link> mainLinkList = new ArrayList<>();
	private Schema schema;

	private SchemaPropagation() {
	}

	/**
	 * This method propagates component's schema-map to its successor components.
	 * 
	 * @param component
	 * @param schemaMap
	 */
	public void continuousSchemaPropagation(Component component, Map<String, ComponentsOutputSchema> schemaMap) {
		LOGGER.debug("Initiating recursive schema propagation");

		if (component != null && schemaMap != null)
			if (StringUtils.equals(Constants.SUBJOB_COMPONENT_CATEGORY, component.getCategory()))
				appplySchemaToTargetComponentsFromSchemaMap(component, schemaMap, Constants.FIXED_OUTSOCKET_ID);
			else
				appplySchemaToTargetComponentsFromSchemaMap(component, schemaMap, null);

		flushLinkLists();
	}
    
	
	/**
	 * @param component
	 * 
	 * This method adds previous component schema to current component property.
	 * 
	 */
	public void addOldSchemaMapPropertyToEachComponent(Component component)
	{
		Map<String,Schema> oldSchemaMap=new TreeMap<String,Schema>();
		for(Link link:component.getTargetConnections())
		{
			Schema schema=(Schema)link.getSource().getProperties().get(Constants.SCHEMA);
			oldSchemaMap.put(link.getTargetTerminal(), schema);
		}	
		component.getProperties().put(Constants.PREVIOUS_COMPONENT_OLD_SCHEMA, oldSchemaMap);
	}
	
	private void flushLinkLists() {
		mainLinkList.clear();
		componentsLinkList.clear();
	}

	private void appplySchemaToTargetComponentsFromSchemaMap(Component destinationComponent,
			Map<String, ComponentsOutputSchema> schemaMap, String targetTerminal) {

		if (StringUtils.isNotEmpty(targetTerminal)) {
			applySchemaToTargetComponents(destinationComponent, targetTerminal, schemaMap.get(targetTerminal));

		} else {
			applySchemaToTargetComponents(destinationComponent, null, schemaMap.get(Constants.FIXED_OUTSOCKET_ID));
		}
	}

	private void applySchemaToTargetComponents(Component destinationComponent, String targetTerminal,
			ComponentsOutputSchema componentsOutputSchema) {
		LOGGER.debug("Applying Schema to :" + destinationComponent.getComponentLabel());
		if (!isInputSubJobComponent(destinationComponent)
				&& StringUtils.equals(Constants.SUBJOB_COMPONENT_CATEGORY, destinationComponent.getCategory())
				&& targetTerminal != null) {
			propagateSchemaFromSubJob(destinationComponent, targetTerminal, componentsOutputSchema);
		}
		setSchemaMapOfComponent(destinationComponent, componentsOutputSchema);
		if (destinationComponent != null && destinationComponent.getSourceConnections().isEmpty()) {
//			mainLinkList.clear();
			return;
		}
		if (!StringUtils.equals(Constants.SUBJOB_COMPONENT_CATEGORY, destinationComponent.getCategory())
				|| isInputSubJobComponent(destinationComponent)) {
			for (Link link : destinationComponent.getSourceConnections()) {
				applySchemaToLinkedComponents(link,componentsOutputSchema);
			}
		}
	}

	
	private void applySchemaToLinkedComponents(Link link, ComponentsOutputSchema componentsOutputSchema) {
		if ((!(Constants.TRANSFORM.equals(link.getTarget().getCategory()) & !Constants.FILTER.equalsIgnoreCase(link
				.getTarget().getComponentName()) & !Constants.PARTITION_BY_EXPRESSION.equalsIgnoreCase(link
						.getTarget().getComponentName())) && !link.getTarget().getProperties()
				.containsValue(componentsOutputSchema))) {
			if (!checkUnusedSocketAsSourceTerminal(link))
				applySchemaToTargetComponents(link.getTarget(), link.getTargetTerminal(), componentsOutputSchema);
			else {
				getComponentsOutputSchema(link);
				applySchemaToTargetComponents(link.getTarget(), link.getTargetTerminal(), this.componentsOutputSchema);
			}
		} else if (Constants.UNIQUE_SEQUENCE.equals(link.getTarget().getComponentName())) {
			propagateSchemForUniqueSequenceComponent(link.getTarget(), componentsOutputSchema);
		} else {
			for (Link link2 : link.getTarget().getSourceConnections()) {
				if (!isMainLinkChecked(link2)) {
					if (checkUnusedSocketAsSourceTerminal(link2) && getComponentsOutputSchema(link2) != null) {
						applySchemaToLinkedComponents(link2,this.componentsOutputSchema);
					} else
						propagatePassThroughAndMapFields(link);
				} else
					break;
			}
		}
	}
	
	
	private ComponentsOutputSchema getComponentsOutputSchemaFromMap(Component destinationComponent,
			Link link, ComponentsOutputSchema componentsOutputSchema) {
		Map<String , ComponentsOutputSchema> schemaMap=(Map<String, ComponentsOutputSchema>) destinationComponent.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
		if(isInputSubJobComponent(destinationComponent) && schemaMap!=null){
			componentsOutputSchema = schemaMap.get(link.getSourceTerminal());	
		}
		return componentsOutputSchema ;
	}

	private boolean isInputSubJobComponent(Component component) {
		if (StringUtils.equals(Constants.INPUT_SUBJOB, component.getComponentName()))
			return true;
		return false;
	}

	private void setSchemaMapOfComponent(Component component, ComponentsOutputSchema componentsOutputSchema) {
		LOGGER.debug("Storing Component-Output-Schema to component :"+component.getComponentLabel().getLabelContents());
		if (!StringUtils.equals(Constants.SUBJOB_COMPONENT_CATEGORY, component.getCategory())) {
			Map<String, ComponentsOutputSchema> newComponentsOutputSchemaMap = new LinkedHashMap<String, ComponentsOutputSchema>();
			if (componentsOutputSchema == null) {
				newComponentsOutputSchemaMap.put(Constants.FIXED_OUTSOCKET_ID, new ComponentsOutputSchema());
			} else {
				newComponentsOutputSchemaMap.put(Constants.FIXED_OUTSOCKET_ID, componentsOutputSchema.copy());
			}
			component.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, newComponentsOutputSchemaMap);
		}
	}

	private void propagateSchemaFromSubJob(Component subJobComponent, String targetTerminal,
			ComponentsOutputSchema componentsOutputSchema) {
		if (componentsOutputSchema == null)
			return;
		
		String outPutTargetTerminal = getTagetTerminalForSubjob(targetTerminal);
		if (subJobComponent.getProperties().get(Constants.INPUT_SUBJOB) != null) {
			Component inputSubjobComponent = (Component) subJobComponent.getProperties().get(
					Constants.INPUT_SUBJOB);
			Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) inputSubjobComponent
					.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
			schemaMap.put(outPutTargetTerminal, componentsOutputSchema);
			for (Link link : inputSubjobComponent.getSourceConnections()) {
				if (StringUtils.equals(link.getSourceTerminal(), outPutTargetTerminal))
					 applySchemaToLinkedComponents(link,componentsOutputSchema);
			}
		}

		else if (StringUtils.equals(Constants.OUTPUT_SUBJOB, subJobComponent.getComponentName())) {

			propagateSchemaFromOutputSubjobComponent(subJobComponent, outPutTargetTerminal, componentsOutputSchema);

		}
	}

	private boolean isOutputSubjobComponent(Component subJobComponent) {
		if (StringUtils.equals(Constants.OUTPUT_SUBJOB, subJobComponent.getComponentName()))
			return true;
		return false;
	}

	private void propagateSchemForUniqueSequenceComponent(Component component,
			ComponentsOutputSchema previousComponentOutputSchema) {
		FixedWidthGridRow fixedWidthGridRow = null;
		Map<String, ComponentsOutputSchema> tempSchemaMap = (Map<String, ComponentsOutputSchema>) component
				.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
		if (tempSchemaMap == null)
			tempSchemaMap = new LinkedHashMap<>();
		ComponentsOutputSchema uniqeSequenceOutputSchema = tempSchemaMap.get(Constants.FIXED_OUTSOCKET_ID);
		if (uniqeSequenceOutputSchema != null
				&& !uniqeSequenceOutputSchema.getFixedWidthGridRowsOutputFields().isEmpty()) {
			fixedWidthGridRow = uniqeSequenceOutputSchema.getFixedWidthGridRowsOutputFields().get(
					uniqeSequenceOutputSchema.getFixedWidthGridRowsOutputFields().size() - 1);
			uniqeSequenceOutputSchema.copySchemaFromOther(previousComponentOutputSchema);
			if (!uniqeSequenceOutputSchema.getFixedWidthGridRowsOutputFields().contains(fixedWidthGridRow))
				uniqeSequenceOutputSchema.getFixedWidthGridRowsOutputFields().add(fixedWidthGridRow);
			applySchemaToTargetComponents(component, null, uniqeSequenceOutputSchema);
		} else
			for (Link linkFromCurrentComponent : component.getTargetConnections()) {
				applySchemaToTargetComponents(linkFromCurrentComponent.getTarget(), null, previousComponentOutputSchema);
			}
	}

	private void propagatePassThroughAndMapFields(Link link) {
		boolean toPropagate = false;
		ComponentsOutputSchema targetOutputSchema = getTargetComponentsOutputSchemaFromMap(link);
		if (targetOutputSchema != null && !targetOutputSchema.getPassthroughFields().isEmpty()) {
			targetOutputSchema.updatePassthroughFieldsSchema(getComponentsOutputSchema(link));
			toPropagate = true;
		}
		if (targetOutputSchema != null && !targetOutputSchema.getMapFields().isEmpty()) {
			targetOutputSchema.updateMapFieldsSchema(getComponentsOutputSchema(link));
			toPropagate = true;
		}
		if (toPropagate)
			applySchemaToTargetComponents(link.getTarget(), null, targetOutputSchema);
	}

	/**
	 * This method retrieves schema from source component
	 * 
	 * @param link
	 * @return ComponentsOutputSchema, the componentsOutputSchema is output schema of component.
	 */
	public ComponentsOutputSchema getComponentsOutputSchema(Link link) {
		LOGGER.debug("Getting Source Output Schema for component.");
		this.componentsOutputSchema = null;
		getSourceSchemaForUnusedPorts(link);
		componentsLinkList.clear();
		return this.componentsOutputSchema;
	}
	
	/**
	 * This method retrieves schema property from source component
	 * 
	 * @param link
	 * @return Schema, the Schema is output schema of component.
	 */
	public Schema getSchema(Link link) {
		LOGGER.debug("Getting Source Output Schema for component.");
		this.schema = null;
		getSchemaFromUnusedPorts(link);
		componentsLinkList.clear();
		return this.schema;
	}

	
	private void getSchemaFromUnusedPorts(Link link) {
		LOGGER.debug("Reverse itration for fetching source schema for component.");
		String socketId = link.getSourceTerminal();
		if (isLinkChecked(link))
			return;
		if (!checkUnusedSocketAsSourceTerminal(link)) {
			this.schema = getSchemaFromLink(link);
			return;
		}
		for (Link link2 : link.getSource().getTargetConnections()) {
			if (link2.getTargetTerminal().equals(getInSocketForUnusedSocket(socketId))) {
				getSchemaFromUnusedPorts(link2);
			}
		}

	}
	
	private Schema getSchemaFromLink(Link link) {
		Schema schema=null;
		if (link != null && link.getSource() != null) {
		schema = (Schema) link.getSource().getProperties().get(Constants.SCHEMA);
		}
		return schema;
	}
	
	
	private void getSourceSchemaForUnusedPorts(Link link) {
		LOGGER.debug("Reverse propagation for fetching source schema for component.");
		String socketId = link.getSourceTerminal();
		if (isLinkChecked(link))
			return;
		if (!checkUnusedSocketAsSourceTerminal(link)) {
			this.componentsOutputSchema = getSourceComponentsOutputSchemaFromMap(link);
			return;
		}
		for (Link link2 : link.getSource().getTargetConnections()) {
			if (link2.getTargetTerminal().equals(getInSocketForUnusedSocket(socketId))) {
				getSourceSchemaForUnusedPorts(link2);
			}
		}

	}

	private boolean isLinkChecked(Link link) {
		if (componentsLinkList.contains(link)) {
			componentsLinkList.clear();
			return true;
		}
		componentsLinkList.add(link);
		return false;
	}

	private boolean isMainLinkChecked(Link link) {
		if (mainLinkList.contains(link)) {
			return true;
		}
		mainLinkList.add(link);
		return false;
	}

	public String getInSocketForUnusedSocket(String unusedSocketId) {
		String unusedPortNo = unusedSocketId.substring(6);
		String inSocket = Constants.INPUT_SOCKET_TYPE + unusedPortNo;
		return inSocket;
	}

	public boolean checkUnusedSocketAsSourceTerminal(Link link) {
		LOGGER.debug("Checking whether link is connected to unused port");
		if (link.getSource().getPort(link.getSourceTerminal()) != null
				&& link.getSource().getPort(link.getSourceTerminal()).getPortType()
						.equals(Constants.UNUSED_SOCKET_TYPE))
			return true;
		return false;
	}

	private ComponentsOutputSchema getSourceComponentsOutputSchemaFromMap(Link link) {
		ComponentsOutputSchema componentsOutputSchema = null;
		if (link != null && link.getSource() != null) {
			Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) link.getSource()
					.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
			if(schemaMap != null && StringUtils.equals(Constants.PARTITION_BY_EXPRESSION, link.getSource().getComponentName())
					&& schemaMap.get(Constants.FIXED_OUTSOCKET_ID) != null){
				componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);
			}
			else if (schemaMap != null && schemaMap.get(link.getSourceTerminal()) != null)
				componentsOutputSchema = schemaMap.get(link.getSourceTerminal());
		}
		return componentsOutputSchema;
	}

	private ComponentsOutputSchema getTargetComponentsOutputSchemaFromMap(Link link) {
		ComponentsOutputSchema componentsOutputSchema = null;
		if (link != null && link.getTarget() != null) {
			Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) link.getTarget()
					.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
			if (schemaMap != null && schemaMap.get(Constants.FIXED_OUTSOCKET_ID) != null)
				componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);
			else if (schemaMap != null && schemaMap.get(Constants.FIXED_OUTSOCKET_ID) != null
					&& StringUtils.equals(Constants.PARTITION_BY_EXPRESSION, link.getSource().getComponentName()))
				componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);	
		}
		return componentsOutputSchema;
	}

	private String getTagetTerminalForSubjob(String targetTerminal) {
		String targetTerminalForSubjob = Constants.FIXED_OUTSOCKET_ID;
		if (StringUtils.isNotEmpty(targetTerminal))
			targetTerminalForSubjob = targetTerminal.replace(Constants.INPUT_SOCKET_TYPE,
					Constants.OUTPUT_SOCKET_TYPE);
		return targetTerminalForSubjob;

	}

	private void propagateSchemaFromOutputSubjobComponent(Component outputSubjobComponent, String targetTerminal,
			ComponentsOutputSchema componentsOutputSchema) {

		Component parentSubjob = (Component) outputSubjobComponent.getProperties().get(Constants.SUBJOB_COMPONENT);
		Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) outputSubjobComponent
				.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
		if (schemaMap != null)
			schemaMap.put(targetTerminal, componentsOutputSchema);
		if (parentSubjob != null) {
			parentSubjob.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);
			for (Link link : parentSubjob.getSourceConnections()) {
				if (StringUtils.equals(link.getSourceTerminal(), targetTerminal))
					 applySchemaToLinkedComponents(link,componentsOutputSchema);
			}
		}
	}
	/**
	 * 
	 * Convert GridRow object to BasicSchemaGridRow object.
	 * 
	 * @param list of GridRow object.
	 * @return list of BasicSchemaGridRow object.
	 */
	public List<FixedWidthGridRow> convertGridRowsSchemaToFixedSchemaGridRows(List<GridRow> gridRows) {
		List<FixedWidthGridRow> basicSchemaGridRows = null;
		if (gridRows != null) {
			basicSchemaGridRows = new ArrayList<>();
			for (GridRow gridRow1 : gridRows) {
				basicSchemaGridRows.add(convertGridRowSchemaToFixedSchemaGridRow(gridRow1));
			}
		}
		return basicSchemaGridRows;
	}

	private FixedWidthGridRow convertGridRowSchemaToFixedSchemaGridRow(GridRow gridRow) {
		FixedWidthGridRow schemaGrid = null;
		if (gridRow != null) {
			schemaGrid = new FixedWidthGridRow();
			schemaGrid.setDataType(gridRow.getDataType());
			schemaGrid.setDataTypeValue(gridRow.getDataTypeValue());
			schemaGrid.setDateFormat(gridRow.getDateFormat());
			schemaGrid.setPrecision(gridRow.getPrecision());
			schemaGrid.setFieldName(gridRow.getFieldName());
			schemaGrid.setScale(gridRow.getScale());
			schemaGrid.setScaleType(gridRow.getScaleType());
			schemaGrid.setScaleTypeValue(gridRow.getScaleTypeValue());
			schemaGrid.setDescription(gridRow.getDescription());
		}
		return schemaGrid;
	}
	
	/**
	 * This method converts current fixed width object into schema grid.
	 * 
	 * @param fixedWidthGridRow
	 * @return SchemaGrid
	 */
	public BasicSchemaGridRow convertFixedWidthSchemaToSchemaGridRow(FixedWidthGridRow fixedWidthGridRow) {
		BasicSchemaGridRow schemaGrid = null;
		if (fixedWidthGridRow != null) {
			schemaGrid = new BasicSchemaGridRow();
			schemaGrid.setDataType(fixedWidthGridRow.getDataType());
			schemaGrid.setDataTypeValue(fixedWidthGridRow.getDataTypeValue());
			schemaGrid.setDateFormat(fixedWidthGridRow.getDateFormat());
			schemaGrid.setPrecision(fixedWidthGridRow.getPrecision());
			schemaGrid.setFieldName(fixedWidthGridRow.getFieldName());
			schemaGrid.setScale(fixedWidthGridRow.getScale());
			schemaGrid.setScaleType(fixedWidthGridRow.getScaleType());
			schemaGrid.setScaleTypeValue(fixedWidthGridRow.getScaleTypeValue());
			schemaGrid.setDescription(fixedWidthGridRow.getDescription());
		}
		return schemaGrid;
	}
	
	public MixedSchemeGridRow convertFixedWidthSchemaToMixedSchemaGridRow(
			FixedWidthGridRow fixedWidthGridRow) {
		MixedSchemeGridRow mixedSchemeGridRow=new MixedSchemeGridRow();
		mixedSchemeGridRow.setDataType(fixedWidthGridRow.getDataType());
		mixedSchemeGridRow.setDataTypeValue(fixedWidthGridRow.getDataTypeValue());
		mixedSchemeGridRow.setDateFormat(fixedWidthGridRow.getDateFormat());
		mixedSchemeGridRow.setDescription(fixedWidthGridRow.getDescription());
		mixedSchemeGridRow.setFieldName(fixedWidthGridRow.getFieldName());
		mixedSchemeGridRow.setLength(fixedWidthGridRow.getLength());
		mixedSchemeGridRow.setPrecision(fixedWidthGridRow.getPrecision());
		mixedSchemeGridRow.setScale(fixedWidthGridRow.getScale());
		mixedSchemeGridRow.setScaleType(fixedWidthGridRow.getScaleType());
		mixedSchemeGridRow.setScaleTypeValue(fixedWidthGridRow.getScaleTypeValue());
		mixedSchemeGridRow.setDelimiter("");
	return mixedSchemeGridRow;
	}
	
	public List<GridRow> getSchemaGridOutputFields(GridRow gridRow,List<FixedWidthGridRow> fixedWidthGridRowsOutputFields) {
		List<GridRow> schemaGrid = new ArrayList<>();
		
		if (gridRow instanceof MixedSchemeGridRow) {
			for (FixedWidthGridRow fixedWidthGridRow : fixedWidthGridRowsOutputFields) {
				schemaGrid.add(convertFixedWidthSchemaToMixedSchemaGridRow(fixedWidthGridRow));
			}
		} else if(gridRow instanceof FixedWidthGridRow){
			for (FixedWidthGridRow fixedWidthGridRow : fixedWidthGridRowsOutputFields) {
				schemaGrid.add(fixedWidthGridRow);
			}
		}else {
			for (FixedWidthGridRow fixedWidthGridRow : fixedWidthGridRowsOutputFields) {
				schemaGrid.add(convertFixedWidthSchemaToSchemaGridRow(fixedWidthGridRow));
			}
		}
			
		
		return schemaGrid;
	}
    
	/**
	 * This methods returns schema-grid row of given field-name.
	 * 
	 * @param fieldName
	 * @return
	 */
	public GridRow getSchemaGridRow(GridRow gridRow,List<FixedWidthGridRow> fixedWidthGridRowsOutputFields) {
		GridRow schemaGridRow = null;
		if (StringUtils.isNotEmpty(gridRow.getFieldName())) {
		for (GridRow row : this.getSchemaGridOutputFields(gridRow,fixedWidthGridRowsOutputFields))
			if (StringUtils.equals(gridRow.getFieldName(), row.getFieldName()))
				schemaGridRow = row;
		}
		return schemaGridRow;
	}

	
}
