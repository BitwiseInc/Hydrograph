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

public class SequenceFieldPropagation {

	public static final SequenceFieldPropagation INSTANCE=new SequenceFieldPropagation();
	
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
