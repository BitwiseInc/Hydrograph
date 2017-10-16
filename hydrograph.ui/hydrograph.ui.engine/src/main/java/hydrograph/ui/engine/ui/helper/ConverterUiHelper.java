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

 
package hydrograph.ui.engine.ui.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.SchemaFieldUtil;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.repository.UIComponentRepo;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.GridRowLoader;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

/**
 * The class ConverterUiHelper
 * 
 * @author Bitwise
 * 
 *         This is a helper class for converter implementation. Contains the helper methods for conversion.
 */
public class ConverterUiHelper {

	protected Map<String, Object> properties = new LinkedHashMap<String, Object>();
	protected Component uIComponent = null;
	protected String componentName = null;

	/**
	 * Instantiate ConverterUiHelper
	 * 
	 * @param component
	 */
	public ConverterUiHelper(Component component) {
		this.uIComponent = component;
		this.properties = component.getProperties();
		this.componentName = (String) properties.get(Constants.PARAM_NAME);
	}

	/**
	 * Create Fixed-Width-Schema for the components.
	 * 
	 * @param record
	 * @return FixedWidthGridRow, this object is responsible for displaying fixed-width schema on property window
	 */
	public FixedWidthGridRow getFixedWidthSchema(Object record) {
		if(record!=null){
		if ((TypeExternalSchema.class).isAssignableFrom(record.getClass())) {
			return null;
		} else if ((TypeBaseField.class).isAssignableFrom(record.getClass())) {
			FixedWidthGridRow fixedWidthGrid = new FixedWidthGridRow();
			TypeBaseField typeBaseField = (TypeBaseField) record;
			getCommonSchema(fixedWidthGrid, typeBaseField);
			fixedWidthGrid.setLength(getStringValue(getQnameValue(typeBaseField, Constants.LENGTH_QNAME)));
			return fixedWidthGrid;
		}}
		return null;
	}
	
	/**
	 * Create File-Mixed-Scheme for the components.
	 * 
	 * @param record
	 * @return MixedSchemeGridRow, this object is responsible for displaying mixed-scheme on property window
	 */
	public MixedSchemeGridRow getMixedScheme(Object record) {
		if ((TypeExternalSchema.class).isAssignableFrom(record.getClass())) {
			return null;
		} else if ((TypeBaseField.class).isAssignableFrom(record.getClass())) {
			MixedSchemeGridRow mixedSchemeGrid = new MixedSchemeGridRow();
			TypeBaseField typeBaseField = (TypeBaseField) record;
			getCommonSchema(mixedSchemeGrid, typeBaseField);
			mixedSchemeGrid.setLength(getStringValue(getQnameValue(typeBaseField, Constants.LENGTH_QNAME)));
			mixedSchemeGrid.setDelimiter(getStringValue(getQnameValue(typeBaseField, Constants.DELIMITER_QNAME)));
			return mixedSchemeGrid;
		}
		return null;
	}

	/**
	 * Create Schema for the components.
	 * 
	 * @param record
	 * @return SchemaGrid, this object is responsible for displaying schema on property window
	 */
	public BasicSchemaGridRow getSchema(Object record) {
		if ((TypeExternalSchema.class).isAssignableFrom(record.getClass())) {
			return null;
		} else if ((TypeBaseField.class).isAssignableFrom(record.getClass())) {
			BasicSchemaGridRow schemaGrid = new BasicSchemaGridRow();
			TypeBaseField typeBaseField = (TypeBaseField) record;
			getCommonSchema(schemaGrid, typeBaseField);
			return schemaGrid;
		}
		return null;
	}
	
	public void getCommonSchema(GridRow gridRow, TypeBaseField typeBaseField) {
		if (typeBaseField != null) {
			if (typeBaseField.getType() != null) {
				gridRow.setDataTypeValue(getStringValue(typeBaseField.getType().value()));
				gridRow.setDataType(GridWidgetCommonBuilder.getDataTypeByValue(typeBaseField.getType().value()));
			}
			
			gridRow.setDateFormat(getStringValue(typeBaseField.getFormat()));
			gridRow.setFieldName(getStringValue(typeBaseField.getName()));
			gridRow.setScale(getStringValue(String.valueOf(typeBaseField.getScale())));
			gridRow.setPrecision(getStringValue(String.valueOf(typeBaseField.getPrecision())));
			gridRow.setDescription(getStringValue(typeBaseField.getDescription()));
			
			if (typeBaseField.getScaleType() != null) {
				gridRow.setScaleType(GridWidgetCommonBuilder
						.getScaleTypeByValue(typeBaseField.getScaleType().value()));
				gridRow.setScaleTypeValue(typeBaseField.getScaleType().value());
			}else if(StringUtils.equals(gridRow.getDataTypeValue(),BigDecimal.class.getName() )){
				gridRow.setScaleType(GridWidgetCommonBuilder.getScaleTypeByValue(Constants.EXPLICIT_SCALE_TYPE_VALUE));
				gridRow.setScaleTypeValue(Constants.EXPLICIT_SCALE_TYPE_VALUE);
			}
		}
	}

	

	/**
	 * Create empty string for null values.
	 * 
	 * @param value
	 * @return
	 */
	public String getStringValue(String value) {
		return SchemaFieldUtil.INSTANCE.getStringValue(value);
	}

	/**
	 * Fetches value of given Qname from component's schema
	 * 
	 * @param typeBaseField
	 *            engine component's schema object
	 * @param qname
	 *            qualified name of an schema field
	 * @return String value of given Qname
	 */
	public String getQnameValue(TypeBaseField typeBaseField, String qname) {
		for (Entry<QName, String> entry : typeBaseField.getOtherAttributes().entrySet()) {
			if (entry.getKey().toString().equals(qname))
				return entry.getValue();
		}
		return null;
	}
	
	/**
	**
	 * This methods loads schema from external schema file
	 * 
	 * @param externalSchemaFilePath
	 * @param schemaType
	 * @return
	 */
	public List<GridRow> loadSchemaFromExternalFile(String externalSchemaFilePath,String schemaType) {
		IPath filePath=new Path(externalSchemaFilePath);
		IPath copyOfFilePath=filePath;
		if (!filePath.isAbsolute()) {
			filePath = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath).getRawLocation();
		}
		if(filePath!=null && filePath.toFile().exists()){
		GridRowLoader gridRowLoader=new GridRowLoader(schemaType, filePath.toFile());
		return gridRowLoader.importGridRowsFromXML();
		
		}else{
			MessageBox messageBox=new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR);
			messageBox.setMessage(Messages.FAILED_TO_IMPORT_SCHEMA_FILE+"\n"+copyOfFilePath.toString());
			messageBox.setText(Messages.ERROR);
			messageBox.open();
		}
		return null;
	}
	
	public static String getFromSocketId(LinkingData linkingData, UIComponentRepo componentRepo) {
		String inSocketId = linkingData.getSourceTerminal();

		if (componentRepo.getComponentUiFactory().get(linkingData.getSourceComponentId()).getComponentName().equals("InputSubjobComponent")) {
			return inSocketId.replace("in", "out");
		}
		return inSocketId;
	}

	public static MappingSheetRow getOperationOrExpression(String operationId, TransformMapping transformMapping,
			boolean isExpression, String componentName) {
		for (MappingSheetRow operationOrExpression : transformMapping.getMappingSheetRows()) {
			if (StringUtils.equalsIgnoreCase(operationId, operationOrExpression.getOperationID())) {
				return operationOrExpression;
			}
		}
		MappingSheetRow mappingSheetRow = new MappingSheetRow(new ArrayList<FilterProperties>(),
				new ArrayList<FilterProperties>(), "", "", false, operationId, new ArrayList<NameValueProperty>(),
				isExpression, new ExpressionEditorData("",componentName),
				new ExpressionEditorData("", componentName), true);

		transformMapping.getMappingSheetRows().add(mappingSheetRow);
		return mappingSheetRow;
	}

}
