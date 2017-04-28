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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.model.Component;

/**
 * This class is used to propagate sequence field of unique sequence component.
 * 
 * @author Bitwise
 *
 */
public class SequenceFieldPropagation {

	public static final SequenceFieldPropagation INSTANCE=new SequenceFieldPropagation();
	
	/**
	 * This method loads the sequence field and propagate into the schema.
	 * 
	 * @param internalSchemaGridRows
	 * @param component
	 * @param textBox
	 * @param newFieldSchema
	 */
	public void loadAndPropagateSequenceField(List<GridRow> internalSchemaGridRows, Component component, Text textBox,
			BasicSchemaGridRow newFieldSchema) {
		String previousValue = (String) component.getProperties().get(Constants.UNIQUE_SEQUENCE_PROPERTY_NAME);
		if (StringUtils.isNotBlank(previousValue)) {
			removePreviousSequenceFieldFromSchema(internalSchemaGridRows, previousValue);
		}
		Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA);

		if (schema != null && internalSchemaGridRows.isEmpty()) {
			removePreviousSequenceFieldFromSchema(schema.getGridRow(), previousValue);
			internalSchemaGridRows.addAll(schema.getGridRow());
		}
		if (StringUtils.isNotBlank(textBox.getText())) {
			internalSchemaGridRows.add(newFieldSchema);
		}
		if (internalSchemaGridRows != null && !internalSchemaGridRows.isEmpty()) {
			Map<String, ComponentsOutputSchema> schemaMap = new HashMap<>();
			ComponentsOutputSchema componentsOutputSchema = new ComponentsOutputSchema();
			List<FixedWidthGridRow> fixedWidthGridRows = SchemaPropagation.INSTANCE
					.convertGridRowsSchemaToFixedSchemaGridRows(internalSchemaGridRows);
			componentsOutputSchema.setFixedWidthGridRowsOutputFields(fixedWidthGridRows);
			schemaMap.put(Constants.FIXED_OUTSOCKET_ID, componentsOutputSchema);
			component.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);
		}
	}

	private void removePreviousSequenceFieldFromSchema(List<GridRow> gridRows, String previousValue) {
		if (!gridRows.isEmpty()) {
			GridRow gridRow = gridRows.get(gridRows.size() - 1);
			if (StringUtils.equalsIgnoreCase(gridRow.getFieldName(), previousValue)
					&& StringUtils.equalsIgnoreCase(Long.class.getCanonicalName(), gridRow.getDataTypeValue())) {
				gridRows.remove(gridRow);
			}
		}
	}
}
