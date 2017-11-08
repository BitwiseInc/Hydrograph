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


package hydrograph.ui.propertywindow.widgets.customwidgets.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.Fields;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.common.util.SchemaFieldUtil;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.schema.propagation.SchemaPropagation;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.Activator;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.filemixedschema.ELTMixedSchemeWidget;
import hydrograph.ui.propertywindow.fixedwidthschema.ELTFixedWidget;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.SchemaConfig;
import hydrograph.ui.propertywindow.widgets.dialogs.GenericImportExportFileDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTRadioButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTTable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTTableViewer;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTSchemaSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTSchemaTableComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.grid.ELTGridDetails;
import hydrograph.ui.propertywindow.widgets.listeners.grid.GridChangeListener;
import hydrograph.ui.propertywindow.widgets.utility.GridComparator;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;
import hydrograph.ui.propertywindow.widgets.utility.SchemaButtonsSyncUtility;
import hydrograph.ui.propertywindow.widgets.utility.SchemaRowValidation;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;
import hydrograph.ui.propertywindow.widgets.utility.SubjobUtility;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;


/**
 * The Class ELTSchemaGridWidget common widget for all component schema grid.
 * 
 * @author Bitwise
 */
public abstract class ELTSchemaGridWidget extends AbstractWidget {

	private static final String COULD_NOT_LOCATE_THE_EXTERNAL_SCHEMA_FILE_PATH = "Could not locate the external file path";
	private static Logger logger = LogFactory.INSTANCE.getLogger(ELTSchemaGridWidget.class);
	//private ColumnLayoutData compositeOfOutsideTable;
	private GridData compositeOfOutsideTable;
	private static final String IMPORT_SCHEMA_FILE_EXTENSION_FILTER = "*.xml;*.schema";
	private static final String IMPORT_SCHEMA_FILE_EXTENSION_NAME = "Schema File (*.xml;*.schema)";
	private static final String EXPORT_XML_FILE_EXTENSION_FILTER = "*.xml";
	private static final String EXPORT_SCHEMA_FILE_EXTENSION_FILTER = "*.schema";
	public static final String FIELDNAME = Messages.FIELDNAME;
	public static final String DATEFORMAT = Messages.DATEFORMAT;
	public static final String DATATYPE = Messages.DATATYPE;
	public static final String PRECISION = Messages.PRECISION;
	public static final String XPATH = Messages.XPATH;
	public static final String SCALE = Messages.SCALE;
	public static final String SCALE_TYPE = Messages.SCALE_TYPE;
	public static final String FIELD_DESCRIPTION = Messages.FIELD_DESCRIPTION;
	public static final String LENGTH = Messages.LENGTH;
	public static final String DELIMITER = Messages.DELIMITER;
	public static int COLUMN_WIDTH=100;
	public static final String RANGE_FROM = Messages.RANGE_FROM;
	public static final String RANGE_TO = Messages.RANGE_TO;
	public static final String DEFAULT_VALUE =Messages.DEFAULT_VALUE;
	public static final String SCHEMA_TAB ="Schema";
	public static final String OPERATION ="operation";
	public static final String NO_SCHEMA_NAME = "The file name is not given";
	public static final String SCHEMA_FILE_EXTENSION = ".schema";
	public static final String XML_FILE_EXTENSION = ".xml";
	private static final int tableHeight=340;
	private static final int tableWidth=360;
	private Integer windowButtonWidth = 35;
	private Integer windowButtonHeight = 25;
	private Integer macButtonWidth = 40;
	private Integer macButtonHeight = 30;

	protected boolean transformSchemaType=false;

	protected String gridRowType;
	private boolean isSchemaValid;
	protected ControlDecoration fieldNameDecorator;
	protected ControlDecoration isFieldNameAlphanumericDecorator;
	protected ControlDecoration scaleDecorator;
	protected ControlDecoration precisionDecorator;
	protected ControlDecoration fieldEmptyDecorator;
	protected ControlDecoration lengthDecorator;
	protected ControlDecoration delimiterDecorator;
	protected ControlDecoration rangeFromDecorator;
	protected ControlDecoration rangeToDecorator;
	protected TableViewer tableViewer=null;
	protected List<GridRow> schemaGridRowList = new ArrayList<GridRow>();
	protected CellEditor[] editors;
	protected Table table;

	protected GridWidgetCommonBuilder gridWidgetBuilder = getGridWidgetBuilder();
	protected Map<String, Integer> columns = getPropertiesToShow();
	protected final String[] PROPS =  populateColumns();
	private Cursor cursor;

	String[] populateColumns(){	
		String[] cols = new String[columns.size()];
		for (Entry<String, Integer> mapEntry : columns.entrySet()) {
			cols[mapEntry.getValue()] = mapEntry.getKey();
		}
		return cols;	
	}

	protected boolean external;
	private Object properties;
	private String propertyName;
	private ListenerHelper helper;

	private ELTDefaultButton addButton , deleteButton,upButton, downButton, importSchemaButton, exportSchemaButton;
	private Button browseButton, importButton, exportButton;

	private MenuItem copyMenuItem, pasteMenuItem;

	protected AbstractELTWidget internalSchema, externalSchema;
	protected Text extSchemaPathText;
	public final static String SCHEMA_CONFIG_XSD_PATH = Platform.getInstallLocation().getURL().getPath() + Messages.SCHEMA_CONFIG_XSD_PATH;

	private ControlDecoration txtDecorator, decorator;

	List<GridRow> copiedGridRows=new ArrayList<GridRow>();

	protected abstract Map<String, Integer> getPropertiesToShow();

	protected abstract GridWidgetCommonBuilder getGridWidgetBuilder();

	protected abstract IStructuredContentProvider getContentProvider();

	protected abstract ITableLabelProvider getLableProvider();

	protected abstract ICellModifier getCellModifier();
	private Composite tableViewerComposite;
	private Composite tableComposite;
	protected String componentType;
	private boolean ctrlKeyPressed = false;
	/**
	 * Adds the validators.
	 */
	 protected abstract void addValidators();


	 /**
	  * Sets the decorator.
	  */
	 //protected abstract void setDecorator(ListenerHelper helper);
	 protected abstract void setDecorator();

	 public ELTSchemaGridWidget() {
		 populateColumns();
	 }

	 /**
	  * Instantiates a new ELT schema grid widget.
	  * 
	  * @param componentConfigrationProperty
	  *            the component configuration property
	  * @param componentMiscellaneousProperties
	  *            the component miscellaneous properties
	  * @param propertyDialogButtonBar
	  *            the property dialog button bar
	  */
	 public ELTSchemaGridWidget(ComponentConfigrationProperty componentConfigrationProperty,
			 ComponentMiscellaneousProperties componentMiscellaneousProperties,
			 PropertyDialogButtonBar propertyDialogButtonBar) {
		 super(componentConfigrationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);
		 componentType=(String)componentMiscellaneousProperties.getComponentMiscellaneousProperty("componentType");
		 this.propertyName = componentConfigrationProperty.getPropertyName();
		 this.properties = componentConfigrationProperty.getPropertyValue();
	 }

	 private List<String> getSchemaFields(List<GridRow> schemaGridRowList2) {
		 List<String> schemaFields = new LinkedList<>();
		 if (schemaGridRowList2 != null) {
			 for (GridRow gridRow : schemaGridRowList2) {
				 GridRow fixedWidthGridRow = gridRow;
				 if (fixedWidthGridRow != null) {
					 schemaFields.add(fixedWidthGridRow.getFieldName());
				 }
			 }
		 }
		 return schemaFields;
	 }

	 @Override
	 public LinkedHashMap<String, Object> getProperties() {
		 
		 if(OSValidator.isMac())
		 {
			for(CellEditor cellEditor : tableViewer.getCellEditors()){
				if(cellEditor !=null){
				 cellEditor.getControl().setEnabled(false); //Saves the existing value of CellEditor
				 cellEditor.getControl().setEnabled(true);
				}
				 }
		 }

		 LinkedHashMap<String, Object> currentSchemaProperty = new LinkedHashMap<>();
		 List<GridRow> schemaGridRowListClone = new ArrayList<>();
		 Map<String, ComponentsOutputSchema> schemaMap = new LinkedHashMap<String, ComponentsOutputSchema>();
		 ComponentsOutputSchema componentsOutputSchema = new ComponentsOutputSchema();
		 propagateInternalSchema();
		
		if (getWidgetConfig() != null && ((SchemaConfig) getWidgetConfig()).doPropagateONOK()) {
			propagateSchemaToNextComponenet(currentSchemaProperty, schemaGridRowListClone, schemaMap,
					componentsOutputSchema);
		} else if (!schemaGridRowList.isEmpty()) {
			for (GridRow gridRow : schemaGridRowList) {
				if (gridRow != null) {
					schemaGridRowListClone.add(gridRow.copy());
				}
			}
		}

		 Schema schema = new Schema();
		 schema.setGridRow(schemaGridRowListClone);
		 if (external) {
			 schema.setIsExternal(true);
			 schema.setExternalSchemaPath(extSchemaPathText.getText());

		 } else {
			 schema.setIsExternal(false);
			 schema.setExternalSchemaPath("");
			 toggleSchema(false);

		 }

		 currentSchemaProperty.put(propertyName, schema);
         return currentSchemaProperty;
	 }

	 private void propagateInternalSchema() {
		 if (schemaGridRowList != null) {
			 if (SchemaSyncUtility.INSTANCE.isAutoSchemaSyncAllow(getComponent()
					 .getComponentName())) {

				 if (SchemaSyncUtility.INSTANCE.isAutoSyncRequiredInMappingWidget(getComponent(),
						 schemaGridRowList)) {

					 MessageDialog dialog = new MessageDialog(new Shell(), Constants.SYNC_WARNING, null, Constants.SCHEMA_NOT_SYNC_MESSAGE, MessageDialog.CONFIRM, new String[] { Messages.SYNC_NOW, Messages.MANUAL_SYNC }, 0);
					 if (dialog.open() == 0) {
						 if(isSchemaUpdated)
						 SchemaSyncUtility.INSTANCE.pushSchemaToMapping(
								 getComponent(), schemaGridRowList);
						 else
						 updateSchemaWithPropogatedSchema(true);	 
					 }
				 }
			 }

		 }
	 }

	 private void propagateSchemaToNextComponenet(
			 LinkedHashMap<String, Object> currentSchema,
			 List<GridRow> schemaGridRowListClone,
			 Map<String, ComponentsOutputSchema> schemaMap,
			 ComponentsOutputSchema componentsOutputSchema) {
		 if (getComponent().getProperties().get(Constants.SCHEMA_TO_PROPAGATE) != null) {

			 ComponentsOutputSchema previousOutputSchema = ((Map<String, ComponentsOutputSchema>) getComponent()
					 .getProperties().get(Constants.SCHEMA_TO_PROPAGATE)).get(Constants.FIXED_OUTSOCKET_ID);

			 if (previousOutputSchema != null && !previousOutputSchema.getMapFields().isEmpty())
				 componentsOutputSchema.getMapFields().putAll(previousOutputSchema.getMapFields());
			 if (previousOutputSchema != null && !previousOutputSchema.getPassthroughFields().isEmpty())
				 componentsOutputSchema.getPassthroughFields().addAll(previousOutputSchema.getPassthroughFields());
			 if (previousOutputSchema != null && !previousOutputSchema.getPassthroughFieldsPortInfo().isEmpty())
				 componentsOutputSchema.getPassthroughFieldsPortInfo().putAll(
						 previousOutputSchema.getPassthroughFieldsPortInfo());
			 if (previousOutputSchema != null && !previousOutputSchema.getMapFieldsPortInfo().isEmpty())
				 componentsOutputSchema.getMapFieldsPortInfo().putAll(previousOutputSchema.getMapFieldsPortInfo());
		 }


		 if (!schemaGridRowList.isEmpty()) {
			 for (GridRow gridRow : schemaGridRowList) {
				 if (gridRow != null) {
					 schemaGridRowListClone.add(gridRow.copy());
					 componentsOutputSchema.addSchemaFields(gridRow);
				 }
			 }
		 }

		 schemaMap.put(Constants.FIXED_OUTSOCKET_ID, componentsOutputSchema);
		 currentSchema.put(Constants.SCHEMA_TO_PROPAGATE,schemaMap);		
		 SchemaPropagation.INSTANCE.continuousSchemaPropagation(getComponent(), schemaMap);
	 }

	 @Override
	 public boolean verifySchemaFile(){
		 boolean verifiedSchema=true;
		 if(external){
			 verifiedSchema=verifyExtSchemaSync(schemaGridRowList);
		 }
		 return verifiedSchema;
	 }

	 private boolean verifyExtSchemaSync(List<GridRow> schemaInGrid) {
		 List<GridRow> schemasFromFile = new ArrayList<GridRow>();
		 File schemaFile=getPath(extSchemaPathText,SCHEMA_FILE_EXTENSION, true, SCHEMA_FILE_EXTENSION,XML_FILE_EXTENSION);
		 if (schemaFile == null){
			 return false;
		 }
		 Fields fields;
		 boolean verifiedSchema = true;
		 try(InputStream  xml = new FileInputStream(schemaFile);
				 InputStream xsd = new FileInputStream(SCHEMA_CONFIG_XSD_PATH);
				 ) {

			 if(validateXML(xml, xsd)){
				 try {
					 
					 	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
						builderFactory.setExpandEntityReferences(false);
						builderFactory.setNamespaceAware(true);
						builderFactory.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION,true);
						
						DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
						Document document=documentBuilder.parse(schemaFile);
						JAXBContext jaxbContext = JAXBContext.newInstance(hydrograph.ui.common.schema.Schema.class);
						Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					 
					 hydrograph.ui.common.schema.Schema schema= 
							 (hydrograph.ui.common.schema.Schema) jaxbUnmarshaller.unmarshal(document);
					 fields = schema.getFields();
					 ArrayList<Field> fieldsList = (ArrayList<Field>) fields.getField();
					 GridRow gridRow = null;

					 if(Messages.GENERIC_GRID_ROW.equals(gridRowType)){

						 for (Field field : fieldsList) {
							 gridRow = new BasicSchemaGridRow();
							 populateCommonFields(gridRow, field);
							 schemasFromFile.add(gridRow);
						 }	

					 }else if(Messages.XPATH_GRID_ROW.equals(gridRowType)){
						 for (Field field : fieldsList) {
							 gridRow = new XPathGridRow();
							 populateCommonFields(gridRow, field);
							 String xpath = field.getAbsoluteOrRelativeXpath();
							 ((XPathGridRow)gridRow).setXPath(StringUtils.isNotBlank(xpath) ? xpath : "");
							 schemasFromFile.add(gridRow);
						 }
					 }else if(Messages.FIXEDWIDTH_GRID_ROW.equals(gridRowType)){

						 for (Field field : fieldsList) {
							 gridRow = new FixedWidthGridRow();
							 populateCommonFields(gridRow, field);

							 if(field.getLength()!=null)
								 ((FixedWidthGridRow) gridRow).setLength(String.valueOf(field.getLength()));
							 else
								 ((FixedWidthGridRow) gridRow).setLength("");
							 schemasFromFile.add(gridRow);
						 }
					 }else if(Messages.MIXEDSCHEME_GRID_ROW.equals(gridRowType)){

						 for (Field field : fieldsList) {
							 gridRow = new MixedSchemeGridRow();
							 populateCommonFields(gridRow, field);
							 if(field.getLength()!=null)
								 ((MixedSchemeGridRow) gridRow).setLength(String.valueOf(field.getLength()));
							 else
								 ((MixedSchemeGridRow) gridRow).setLength("");
							 ((MixedSchemeGridRow) gridRow).setDelimiter(field.getDelimiter());
							 schemasFromFile.add(gridRow);
						 }
					 }else if(Messages.GENERATE_RECORD_GRID_ROW.equals(gridRowType)){

						 for (Field field : fieldsList) {
							 gridRow = new GenerateRecordSchemaGridRow();
							 populateCommonFields(gridRow, field);

							 if(field.getLength()!=null)
								 ((GenerateRecordSchemaGridRow) gridRow).setLength(String.valueOf(field.getLength()));
							 else
								 ((GenerateRecordSchemaGridRow) gridRow).setLength("");

							 if(field.getDefault()!=null)
								 ((GenerateRecordSchemaGridRow) gridRow).setDefaultValue((String.valueOf(field.getDefault())));
							 else
								 ((GenerateRecordSchemaGridRow) gridRow).setDefaultValue((String.valueOf("")));

							 if(field.getRangeFrom()!=null)
								 ((GenerateRecordSchemaGridRow) gridRow).setRangeFrom(String.valueOf(field.getRangeFrom()));
							 else
								 ((GenerateRecordSchemaGridRow) gridRow).setRangeFrom("");

							 if(field.getRangeFrom()!=null)
								 ((GenerateRecordSchemaGridRow) gridRow).setRangeTo(String.valueOf(field.getRangeTo()));
							 else
								 ((GenerateRecordSchemaGridRow) gridRow).setRangeTo("");

							 schemasFromFile.add(gridRow);

						 }

					 }
				 } catch (JAXBException | ParserConfigurationException | SAXException | IOException e) {
					 logger.error(Messages.EXPORTED_SCHEMA_SYNC_ERROR, e);
				 }

			 }
			 if(!equalLists(schemaInGrid, schemasFromFile,new GridComparator())){
				 verifiedSchema=false;
				 //MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", Messages.EXPORTED_SCHEMA_NOT_IN_SYNC);
				 MessageBox dialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
				 dialog.setText(Messages.INFORMATION);
				 dialog.setMessage(Messages.EXPORTED_SCHEMA_NOT_IN_SYNC);
			 }

		 } catch (FileNotFoundException e) {
			 logger.error(Messages.EXPORTED_SCHEMA_SYNC_ERROR ,e);


		 }
		 catch (IOException e){
			 logger.error(Messages.EXPORTED_SCHEMA_SYNC_ERROR ,e);
		 }
		 return verifiedSchema;

	 }

	 private boolean validateXML(InputStream xml, InputStream xsd){
		 try
		 {
			 SchemaFactory factory = 
					 SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			 javax.xml.validation.Schema schema = factory.newSchema(new StreamSource(xsd));
			 Validator validator = schema.newValidator();

			 validator.validate(new StreamSource(xml));
			 return true;
		 }
		 catch( SAXException| IOException ex)
		 {
			 //MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", Messages.IMPORT_XML_FORMAT_ERROR + "-\n" + ex.getMessage());
			 MessageBox dialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			 dialog.setText(Messages.ERROR);
			 dialog.setMessage(Messages.IMPORT_XML_FORMAT_ERROR + "-\n" + ex.getMessage());
			 logger.error(Messages.IMPORT_XML_FORMAT_ERROR);
			 return false;
		 }
	 }

	 private void populateCommonFields(GridRow gridRow, Field temp) {
		 gridRow.setFieldName(temp.getName());
		 gridRow.setDataType(GridWidgetCommonBuilder.getDataTypeByValue(temp.getType().value()));
		 gridRow.setDataTypeValue(GridWidgetCommonBuilder.getDataTypeValue()[GridWidgetCommonBuilder.getDataTypeByValue(temp.getType().value())]);

		 if(temp.getFormat()!=null)
			 gridRow.setDateFormat(temp.getFormat());
		 else
			 gridRow.setDateFormat("");

		 if(temp.getPrecision()!=null)
			 gridRow.setPrecision(String.valueOf(temp.getPrecision()));
		 else
			 gridRow.setPrecision("");

		 if(temp.getScale()!=null)
			 gridRow.setScale(String.valueOf(temp.getScale()));
		 else
			 gridRow.setScale("");

		 if(temp.getScaleType()!=null){
			 gridRow.setScaleType(SchemaFieldUtil.INSTANCE.getScaleTypeByValue(temp.getScaleType().value()));	
			 gridRow.setScaleTypeValue(GridWidgetCommonBuilder.getScaleTypeValue()[SchemaFieldUtil.INSTANCE.getScaleTypeByValue(temp.getScaleType().value())]);
		 }else{
			 gridRow.setScaleType(GridWidgetCommonBuilder.getScaleTypeByValue(Messages.SCALE_TYPE_NONE));
			 gridRow.setScaleTypeValue(GridWidgetCommonBuilder.getScaleTypeValue()[Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX)]);
		 }
		 if(temp.getDescription()!=null)
			 gridRow.setDescription(temp.getDescription());
		 else
			 gridRow.setDescription("");
	 }

	 public  boolean equalLists(List<GridRow> one, List<GridRow> two, GridComparator gridComparator ){     
		 if (one == null && two == null){
			 return true;
		 }

		 if((one == null && two != null) 
				 || one != null && two == null
				 || one.size() != two.size()){
			 return false;
		 }
		 Iterator<GridRow> firstList = one.iterator();
		 Iterator<GridRow> secondList = two.iterator();
		 while (firstList.hasNext()) {
			 GridRow t1 = firstList.next();
			 GridRow t2 = secondList.next();
			 if (gridComparator.compare(t1, t2) != 0) {
				 // as soon as a difference is found, stop looping
				 return false;
			 }
		 }
		 return true;
	 }

	 // Operational class label.
	 AbstractELTWidget fieldError = new ELTDefaultLable(Messages.FIELDNAMEERROR).lableWidth(250);

	 /**
	  * @wbp.parser.entryPoint
	  */
	 @Override
	 public void attachToPropertySubGroup(AbstractELTContainerWidget container) {

		 if((StringUtils.equalsIgnoreCase(getComponent().getCategory(), Constants.STRAIGHTPULL))
				|| (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.FILTER_COMPONENT)
				|| (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.UNIQUE_SEQUENCE_COMPONENT))
				|| (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.PARTITION_BY_EXPRESSION)))
				 ){
			 createSchemaGridSection(container.getContainerControl(),tableHeight, tableWidth);
		 }
		 else if(transformSchemaType)
		 {
			 createButtonComposite(container.getContainerControl());
			 createSchemaGridSection(container.getContainerControl(),tableHeight, tableWidth);
		 }	 
		 else{

			 createButtonComposite(container.getContainerControl());
			 createSchemaGridSection(container.getContainerControl(), 250, 360);
			 createSchemaTypesSection(container.getContainerControl());
			 createExternalSchemaSection(container.getContainerControl());
		 }
		 tableComposite.getShell().addControlListener(new ControlListener() {

			 @Override
			 public void controlResized(ControlEvent e) {
				 Shell shell = (Shell) e.getSource();
				 Rectangle schemaTable = shell.getClientArea();
				 if(OSValidator.isMac()){
					 compositeOfOutsideTable.heightHint = tableHeight + (schemaTable.height - 670);
				 }else{
					 compositeOfOutsideTable.heightHint = tableHeight + (schemaTable.height - 640);
				 }
			 }

			 @Override
			 public void controlMoved(ControlEvent e) {
			 }
		 });

		 populateSchemaTypeWidget();
	 }
	 /**
	  * creates group of add,delete,up,down buttons and pull schema button
	  */
	 private ELTSchemaSubgroupComposite createButtonComposite(Composite containerControl){
		 ELTSchemaSubgroupComposite pull = new ELTSchemaSubgroupComposite(containerControl);
		 pull.createContainerWidget();
		 pull.numberOfBasicWidgets(2);
		 pull.getContainerControl().setLayout(getPullCompositeLayout(1, 2, 2, 5));
		 if(SchemaSyncUtility.INSTANCE.isSchemaSyncAllow(getComponent().getComponentName())){
			 pull.numberOfBasicWidgets(3);
			 pull.getContainerControl().setLayout(getPullCompositeLayout(2, 2, 2, 5));
			 pull.getContainerControl().setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
			 createPullInternallyPropagatedSchema(pull);
		 }
		 if (StringUtils.equalsIgnoreCase(getComponent().getCategory(), Constants.OUTPUT)){
			 pull.numberOfBasicWidgets(3);
			 pull.getContainerControl().setLayout(getPullCompositeLayout(2, 2, 2, 5));
			 pull.getContainerControl().setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
			 createPullPropagtedSchemaButton(pull);
		 }
		 
		 ELTSchemaSubgroupComposite buttonSubGroup = new ELTSchemaSubgroupComposite(pull.getContainerControl());
		 buttonSubGroup.createContainerWidget();
		 buttonSubGroup.numberOfBasicWidgets(4);
		 GridLayout layout = new GridLayout(4, false);
		 layout.horizontalSpacing = -45;
		 layout.marginLeft = 350;
		 layout.marginRight= 0;
		 layout.marginWidth = 0;
		 buttonSubGroup.getContainerControl().setLayout(layout);
		 if (StringUtils.equalsIgnoreCase(getComponent().getCategory(), Constants.OUTPUT)){
			 buttonSubGroup.numberOfBasicWidgets(7);
			 buttonSubGroup.getContainerControl().setLayout(getButtonCompositeLayout(6, 0, 0));
			 ELTDefaultLable defaultLable1 = new ELTDefaultLable("");
			 defaultLable1.lableWidth(0);
			 buttonSubGroup.attachWidget(defaultLable1);
			 ELTDefaultLable defaultLable = new ELTDefaultLable("");
			 buttonSubGroup.attachWidget(defaultLable);
		 }

		if (SchemaSyncUtility.INSTANCE.isSchemaSyncAllow(getComponent().getComponentName())) {
			buttonSubGroup.numberOfBasicWidgets(7);
			buttonSubGroup.getContainerControl().setLayout(getButtonCompositeLayout(6, 0, 0));

			if ((StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.AGGREGATE))
					|| (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.CUMULATE))
					|| (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.GROUP_COMBINE))
					|| (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.NORMALIZE))
					|| (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.TRANSFORM))) {
				addImportSchemaButton(buttonSubGroup);
				addExportSchemaButton(buttonSubGroup);
				
			} else {
				ELTDefaultLable defaultLable1 = new ELTDefaultLable("");
				defaultLable1.lableWidth(0);
				buttonSubGroup.attachWidget(defaultLable1);
				ELTDefaultLable defaultLable = new ELTDefaultLable("");
				buttonSubGroup.attachWidget(defaultLable);
			}
		}
		 
		 if(!(StringUtils.equalsIgnoreCase(getComponent().getCategory(), Constants.OUTPUT))&&!(SchemaSyncUtility.INSTANCE.isSchemaSyncAllow(getComponent().getComponentName()))){
			 buttonSubGroup.numberOfBasicWidgets(7);
			 buttonSubGroup.getContainerControl().setLayout(getButtonCompositeLayout(6, 0, 0));
			 ELTDefaultLable defaultLable2 = new ELTDefaultLable("");
			 defaultLable2.lableWidth(215);
			 buttonSubGroup.attachWidget(defaultLable2);
			 ELTDefaultLable defaultLable1 = new ELTDefaultLable("");
			 defaultLable1.lableWidth(0);
			 buttonSubGroup.attachWidget(defaultLable1);
		 }
		 
		 addAddButton(buttonSubGroup);
		 addDeleteButton(buttonSubGroup);
		 addUpButton(buttonSubGroup);
		 addDownButton(buttonSubGroup);
		 return buttonSubGroup;
	 }
	 
	 /**
	 * retruns GridLayout with given arguments 
	 * @param noOfColumns
	 * @param widthMar
	 * @param rightMar
	 * @param leftMar
	 * @return GridLayout
	 * 
	 */
	private GridLayout getPullCompositeLayout(int noOfColumns,int widthMar,int rightMar,int leftMar){
		 GridLayout pullCompositeLayout = new GridLayout(noOfColumns,false);
		 pullCompositeLayout.marginWidth = widthMar;
		 pullCompositeLayout.marginRight = rightMar;
		 pullCompositeLayout.marginLeft = leftMar;
		 return pullCompositeLayout;
	 }
	 /**
	 * returns GridDataLayout with given arguments 
	 * @param noOfColumns
	 * @param widthMar
	 * @param rightMar
	 * @return
	 */
	private GridLayout getButtonCompositeLayout(int noOfColumns,int widthMar,int rightMar){
		 GridLayout buttonCompositeLayout = new GridLayout(noOfColumns,false);
		 buttonCompositeLayout.marginWidth = widthMar;
		 buttonCompositeLayout.marginRight = rightMar;
		 return buttonCompositeLayout;
	 }
	 /**
	 * Creates PullSchema buttton on schema tab 
	 * @param containerControl
	 */
	private void createPullPropagtedSchemaButton(ELTSchemaSubgroupComposite containerControl) {
		 ELTDefaultButton pullButtonForOuputComponents = new ELTDefaultButton("Pull Schema");
		 pullButtonForOuputComponents.buttonWidth(120);
		 containerControl.attachWidget(pullButtonForOuputComponents);
		 ((Button)pullButtonForOuputComponents.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 schemaFromConnectedLinks();
				 showHideErrorSymbol(isWidgetValid());
				 refresh();

			 }
		 });
		 if(getComponent().getTargetConnections()==null || getComponent().getTargetConnections().isEmpty()){
			 ((Button)pullButtonForOuputComponents.getSWTWidgetControl()).setEnabled(false);
		 }
	 }

	 /**
	  * 
	  * returns propagated schema
	  * 
	  * @param {@link Link}
	  * @return {@link Schema}
	  */
	 private Schema getPropagatedSchema(List<FixedWidthGridRow> fixedWidthGridRows) {
		 Schema schema = new Schema();
		 schema.setExternalSchemaPath("");
		 schema.setIsExternal(false);
		 schema.setGridRow(new ArrayList<GridRow>());
		 if (fixedWidthGridRows != null) {
			 if( this.getClass().isAssignableFrom(ELTMixedSchemeWidget.class))
				 for (FixedWidthGridRow gridRow : fixedWidthGridRows) {
					 schema.getGridRow().add(SchemaPropagation.INSTANCE.convertFixedWidthSchemaToMixedSchemaGridRow(gridRow));
				 }
			 else if (this.getClass().isAssignableFrom(ELTFixedWidget.class)) {
				 for (FixedWidthGridRow gridRow : fixedWidthGridRows) {
					 schema.getGridRow().add(gridRow);
				 }
			 } else if (this.getClass().equals(ELTGenericSchemaGridWidget.class)) {
				 for (FixedWidthGridRow gridRow : fixedWidthGridRows) {
					 schema.getGridRow().add(SchemaPropagation.INSTANCE.convertFixedWidthSchemaToSchemaGridRow(gridRow));
				 }
			 }else if (this.getClass().equals(XPathSchemaGridWidget.class)) {
				
				 for (FixedWidthGridRow gridRow : fixedWidthGridRows) {
					Text loopXpathTextBox=(Text)table.getData();
					XPathGridRow xPathGridRow=SchemaPropagation.INSTANCE.convertFixedWidthSchemaToxPathSchemaGridRow(gridRow);
					if(StringUtils.isNotBlank(loopXpathTextBox.getText())){
						xPathGridRow.setAbsolutexPath(loopXpathTextBox.getText().trim()+Path.SEPARATOR+xPathGridRow.getXPath());
					}
					schema.getGridRow().add(xPathGridRow);
				 }
				 
			 }
			 applySchemaFromPropagatedSchemaOnPull(schema,fixedWidthGridRows);
		 }
		 if (schemaGridRowList != null)
			 enableDisableButtons(schemaGridRowList.size());
		 tableViewer.refresh();
		 return schema;
	 }

	 private void applySchemaFromPropagatedSchemaOnPull(Schema schema, List<FixedWidthGridRow> fixedWidthGridRows) {
		 List<GridRow> copiedList=new ArrayList<>(schemaGridRowList);
		 int returnCode =0;
		 MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES| SWT.CANCEL |SWT.NO);
		 messageBox.setMessage(Messages.MESSAGE_FOR_FETCHING_PROPAGATED_SCHEMA);
		 messageBox.setText(Messages.INFORMATION);
		 returnCode=messageBox.open();
		 if(returnCode!=SWT.CANCEL){
			 if (!schemaGridRowList.isEmpty()) {
				 if ( returnCode== SWT.YES ) {
					 Iterator<GridRow> itr= copiedList.iterator();
					 Iterator <GridRow> itr1= schema.getGridRow().iterator();
					 schemaGridRowList.removeAll(schemaGridRowList);
					 while (itr.hasNext() && itr1.hasNext()){
						 GridRow schemaGridRow = SchemaPropagation.INSTANCE.getSchemaGridRow(itr1.next(),fixedWidthGridRows);
						 if (schemaGridRow == null){
							 schemaGridRowList.remove(itr.next());
						 }
						 else{
							 //if "Yes" is clicked in pull schema then it should update the data type etc. 
							 schemaGridRowList.add(schemaGridRow);
						 }
					 }
				 }
			 }
			 for (GridRow gridRow : schema.getGridRow()){
				 if (!schemaGridRowList.contains(gridRow)){
					 GridRow newGridRow;
					 FixedWidthGridRow fixedWidthGridRow;
					 XPathGridRow xpathGridRow;
					 try {
						 if (Messages.FIXEDWIDTH_GRID_ROW.equals(gridRowType)){												
							 fixedWidthGridRow = new FixedWidthGridRow();
							 fixedWidthGridRow.updateBasicGridRow(gridRow);
							 schemaGridRowList.add(fixedWidthGridRow);
						 }
						 else if(Messages.XPATH_GRID_ROW.equals(gridRowType)){
							 xpathGridRow=new XPathGridRow();
							 xpathGridRow.updateBasicGridRow(gridRow);
							 xpathGridRow.setXPath(gridRow.getFieldName());
							 xpathGridRow.setAbsolutexPath(gridRow.getFieldName());
							 Text loopXpathTextBox=(Text)table.getData();
							 if(StringUtils.isNotBlank(loopXpathTextBox.getText())){
								xpathGridRow.setAbsolutexPath(loopXpathTextBox.getText().trim()+Path.SEPARATOR+xpathGridRow.getXPath());
							 }
							 else{
									xpathGridRow.setAbsolutexPath(xpathGridRow.getXPath());
							 }
							 schemaGridRowList.add(xpathGridRow);
					 	 }
						 else {
							 newGridRow = (GridRow) Class.forName(gridRow.getClass().getCanonicalName()).getDeclaredConstructor().newInstance();
							 newGridRow.updateBasicGridRow(gridRow);
							 schemaGridRowList.add(newGridRow);
						 }
					 }catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							 | InvocationTargetException | NoSuchMethodException | SecurityException
							 | ClassNotFoundException e) {
						 logger.error("Exception occurred while creating new row for schema",e);
					 }
				 }
			 }
			 propertyDialogButtonBar.enableApplyButton(true);
		 }
	 }

	 private void schemaFromConnectedLinks() {
		 Schema schema=null;
		 Map<String,Schema> oldSchemaMap=new TreeMap<>();
		 if (StringUtils.equalsIgnoreCase(getComponent().getCategory(), Constants.OUTPUT))
			 for (Link link : getComponent().getTargetConnections()) {
				 if(SchemaPropagation.INSTANCE.checkUnusedSocketAsSourceTerminal(link)){
					 String unusedSocketId=link.getSourceTerminal();
					 for(Link innerLink:link.getSource().getTargetConnections()){
						 if(innerLink.getTargetTerminal().equals(SchemaPropagation.INSTANCE.getInSocketForUnusedSocket(unusedSocketId))){
							 schema=SubjobUtility.INSTANCE.getSchemaFromPreviousComponentSchema(getComponent(), innerLink); 
							 break;
						 }
					}
				 }
				 else{
					 schema=SubjobUtility.INSTANCE.getSchemaFromPreviousComponentSchema(getComponent(), link);
				 }		 
				 if(schema!=null)
				 {	 
					List<FixedWidthGridRow> fixedWidthGridRows=
							SchemaPropagation.INSTANCE.convertGridRowsSchemaToFixedSchemaGridRows(schema.getGridRow());
					 if (this.properties != null && this.schemaGridRowList != null && !this.schemaGridRowList.isEmpty()) {
					 if (isAnyUpdateAvailableOnPropagatedSchema(schema)) {
						 this.properties = getPropagatedSchema(fixedWidthGridRows);
					 } else {
							showMessage();
					 }
				 } else {
					 this.properties = getPropagatedSchema(fixedWidthGridRows);
				 }
					 oldSchemaMap.put(link.getTargetTerminal(),schema );	
				 }
			 }
		 getComponent().getProperties().put(Constants.PREVIOUS_COMPONENT_OLD_SCHEMA, oldSchemaMap);
	 }

	 private boolean isAnyUpdateAvailableOnPropagatedSchema(Schema schema) {
		 GridRow propagatedGridRow = null;
		 if (this.schemaGridRowList.size() == schema.getGridRow().size()) {
			 List<GridRow> list=schema.getGridRow();

			 Iterator <GridRow> itr=this.schemaGridRowList.iterator();
			 Iterator <GridRow> itr1=list.iterator();
			 while(itr.hasNext() && itr1.hasNext())
			 {
				 propagatedGridRow = itr1.next();
				 if (propagatedGridRow == null || ( propagatedGridRow != null
						 && !SchemaPropagationHelper.INSTANCE.isGridRowEqual(itr.next(), propagatedGridRow)) ){
					 return true;
				 }
			 }
			 return false;
		 }

		 return true;
	 }

	 private boolean isAnyUpdateAvailableformPulledSchema(Schema schema) {
		
		 if (this.schemaGridRowList.size() == schema.getGridRow().size()) {
			 List<GridRow> list=schema.getGridRow();
			 Iterator <GridRow> itr=this.schemaGridRowList.iterator();
			 Iterator <GridRow> itr1=list.iterator();
			 while(itr.hasNext() && itr1.hasNext())
			 {
				 if (!SchemaPropagationHelper.INSTANCE.isGridRowEqual(itr.next(),itr1.next())){
					 return true;
				 }
			 }
			 return false;
		 }

		 return true;
	 }
	 /**
	  * Adds the pull schema button
	  */
	 private void createPullInternallyPropagatedSchema(ELTSchemaSubgroupComposite containerControl) {
		 ELTDefaultButton btnPull = new ELTDefaultButton(Messages.PULL_SCHEMA);
		 btnPull.buttonWidth(120);
		 containerControl.attachWidget(btnPull);
		 ((Button)btnPull.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {				
				 updateSchemaWithPropogatedSchema(true);
			 }
		 });
		 if(getComponent().getTargetConnections()==null || getComponent().getTargetConnections().isEmpty()){
			 ((Button)btnPull.getSWTWidgetControl()).setEnabled(false);
		 }
	 }

	 public void updateSchemaWithPropogatedSchema(boolean showPopoupMessage){
		 if(!getComponent().getCategory().equalsIgnoreCase(Constants.TRANSFORM_DISPLAYNAME)){
			 schemaGridRowList.clear();
		 }
			
		 boolean isUpdate = getCompareSchemaWithInputLink();
		if (!isAnyUpdateAvailableformPulledSchema(getSchemaForInternalPropagation())) {
			if(!isUpdate && showPopoupMessage){
				showMessage();
			}
		} else {
			syncInternallyPropagatedSchema();
			showHideErrorSymbol(applySchemaValidationRule());
			getComponent().setLatestChangesInSchema(true);
			enableDisableButtons(schemaGridRowList.size());

			propertyDialogButtonBar.enableApplyButton(true);
		}
	 }
	 
	 /**
	  * compare schema with input link for transform mapping
	 * @return
	 */
	private boolean getCompareSchemaWithInputLink(){
		 boolean isUpdate = false;
		 List<GridRow> tempSchema = new ArrayList<>();
		 if(schemaGridRowList !=null){
			 tempSchema.addAll(schemaGridRowList);
		 }
		 List<GridRow> gridRows = null;
		 
		 //get linked component schema
		 if(getComponent().getInputLinks() != null){
			 List<Link> links = getComponent().getInputLinks();
			 for(Link link : links){
				 Schema inputLinkSchema = (Schema) link.getSource().getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
				 if(inputLinkSchema!=null){
					 gridRows = inputLinkSchema.getGridRow();
				 }
			 }
		 }
		if(getComponent().getCategory().equalsIgnoreCase(Constants.TRANSFORM_DISPLAYNAME)){
			TransformMapping transformMapping = (TransformMapping) getComponent().getProperties().get("operation");
			if(transformMapping != null && transformMapping.getMapAndPassthroughField() != null){
				List<NameValueProperty> nameValueProperties = transformMapping.getMapAndPassthroughField();
				List<MappingSheetRow> mappingSheetRows = transformMapping.getMappingSheetRows();
				
				///get mappassthrough field and compare with schema tab schema
				if(nameValueProperties != null && gridRows != null){
					for(int index=0;index <= nameValueProperties.size() - 1;index++){
						for(GridRow inputGridRow : gridRows){
							if(StringUtils.equals(inputGridRow.getFieldName(), nameValueProperties.get(index).getPropertyName())){
								for(GridRow schemaGridRow : tempSchema){
									if(StringUtils.equals(schemaGridRow.getFieldName(), nameValueProperties.get(index).getPropertyValue())){
										if(!StringUtils.equals(schemaGridRow.getDataTypeValue(), inputGridRow.getDataTypeValue())){
											BasicSchemaGridRow basicSchemaGridRow = getBasicSchemaFieldValues(inputGridRow);
											schemaGridRowList.remove(basicSchemaGridRow);
											schemaGridRowList.add(basicSchemaGridRow);
											isUpdate = true;
										}
									}
								}
							}
						}
					}
				}
				ELTGridDetails eLTDetails = (ELTGridDetails) helper.get(HelperType.SCHEMA_GRID);
				eLTDetails.setGrids(schemaGridRowList);
				tableViewer.setInput(schemaGridRowList);
				tableViewer.refresh();
			}
		}
		return isUpdate;
	 }
	 
	 private BasicSchemaGridRow getBasicSchemaFieldValues(GridRow inputGridRow){
		 BasicSchemaGridRow tempschemaGrid = new BasicSchemaGridRow();
			tempschemaGrid.setDataType(inputGridRow.getDataType());
			tempschemaGrid.setDateFormat(inputGridRow.getDateFormat());
			tempschemaGrid.setFieldName(inputGridRow.getFieldName());
			tempschemaGrid.setScale(inputGridRow.getScale());
			tempschemaGrid.setDataTypeValue(inputGridRow.getDataTypeValue());
			tempschemaGrid.setScaleType(inputGridRow.getScaleType());
			tempschemaGrid.setScaleTypeValue(inputGridRow.getScaleTypeValue());
			tempschemaGrid.setPrecision(inputGridRow.getPrecision());
			tempschemaGrid.setDescription(inputGridRow.getDescription());
		return tempschemaGrid;
	 }
	 
	 private boolean updateTransformSchemaOnPull(GridRow inputGridRow, String fieldName){
		 boolean isFieldUpdate = false;
		 for(GridRow schemaGridRow : schemaGridRowList){
			 if(!StringUtils.equals(schemaGridRow.getFieldName(), fieldName)){
				 schemaGridRow.setDataType(inputGridRow.getDataType());
				 schemaGridRow.setDataTypeValue(inputGridRow.getDataTypeValue());
				 isFieldUpdate = true;
			 }
		 }
		 return isFieldUpdate;
	 }

	 // Adds the browse button
	 private void createExternalSchemaSection(Composite containerControl) {
		 ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(containerControl);
		 eltSuDefaultSubgroupComposite.createContainerWidget();
		 eltSuDefaultSubgroupComposite.getContainerControl().setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,1,1));
		 eltSuDefaultSubgroupComposite.numberOfBasicWidgets(5);

		 GridLayout gd = new GridLayout(3,false);
		 gd.marginRight = 0;
		 gd.marginWidth = 5;
		 gd.horizontalSpacing = 9;
		 eltSuDefaultSubgroupComposite.getContainerControl().setLayout(gd);

		 AbstractELTWidget eltDefaultLable = new ELTDefaultLable(Messages.EXTERNAL_SCHEMA);
		 eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);

		 AbstractELTWidget eltDefaultTextBox = new ELTDefaultTextBox().grabExcessHorizontalSpace(true).textBoxWidth(218);
		 eltSuDefaultSubgroupComposite.attachWidget(eltDefaultTextBox);

		 extSchemaPathText = (Text) eltDefaultTextBox.getSWTWidgetControl();
		 extSchemaPathText.setToolTipText(Messages.CHARACTERSET);
		 decorator = WidgetUtility.addDecorator(extSchemaPathText, Messages.EMPTYFIELDMESSAGE);
		 decorator.hide();
		 extSchemaPathText.addModifyListener(new ModifyListener() {

			 @Override
			 public void modifyText(ModifyEvent e) {
				 exportButton.setEnabled(StringUtils.isNotBlank(((Text)e.widget).getText()));
				 importButton.setEnabled(StringUtils.isNotBlank(((Text)e.widget).getText()));
			 }
		 });
		 extSchemaPathText.addFocusListener(new FocusListener() {

			 @Override
			 public void focusLost(FocusEvent e) {
				 if (extSchemaPathText.getText().isEmpty()) {
					 decorator.show();
					 extSchemaPathText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 250, 250, 250));
				 } else {
					 decorator.hide();
				 }
			 }

			 @Override
			 public void focusGained(FocusEvent e) {
				 decorator.hide();
				 extSchemaPathText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			 }
		 });

		 AbstractELTWidget eltDefaultButton = new ELTDefaultButton(Messages.BROWSE_BUTTON).buttonWidth(35);
		 eltSuDefaultSubgroupComposite.attachWidget(eltDefaultButton);
		 browseButton = (Button) eltDefaultButton.getSWTWidgetControl();

		 browseButton.addSelectionListener(new SelectionListener() {

			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 decorator.hide();
				 extSchemaPathText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));

			 }

			 @Override
			 public void widgetDefaultSelected(SelectionEvent e) {
				 // Nothing to Do
			 }

		 });


		 txtDecorator = WidgetUtility.addDecorator(extSchemaPathText, Messages.CHARACTERSET);
		 txtDecorator.setMarginWidth(3);
		 decorator.setMarginWidth(3);

		 txtDecorator.hide();


		 helper.put(HelperType.CONTROL_DECORATION, txtDecorator);
		 helper.put(HelperType.FILE_EXTENSION,"schema");

		 try {
			 eltDefaultTextBox.attachListener(ListenerFactory.Listners.EVENT_CHANGE.getListener(),
					 propertyDialogButtonBar, null, eltDefaultTextBox.getSWTWidgetControl());
			 eltDefaultTextBox.attachListener(ListenerFactory.Listners.MODIFY.getListener(), propertyDialogButtonBar,
					 helper, eltDefaultTextBox.getSWTWidgetControl());
			 eltDefaultButton.attachListener(ListenerFactory.Listners.BROWSE_FILE_LISTNER.getListener(),
					 propertyDialogButtonBar, helper,extSchemaPathText);

		 } catch (Exception e1) {
			 e1.printStackTrace();
		 }
		 
		 	Utils.INSTANCE.loadProperties();
			cursor = containerControl.getDisplay().getSystemCursor(SWT.CURSOR_HAND);
				
	    	addImportExportButtons(containerControl);

	    	populateWidgetExternalSchema();
	 }
		
		public static File getPath(Text extSchemaPathText, String defaultExtension, boolean showErrorMessage, String... fileExtensions){
			 File schemaFile=null;
			 String schemaPath =null;
			 String finalParamPath=null;
			 IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();

			 if(input instanceof IFileEditorInput){
				 
				 if(ParameterUtil.containsParameter(extSchemaPathText.getText(), '/')){
					 String paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText.getText());
					 finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText.getText(), paramValue, extSchemaPathText);
						while(ParameterUtil.containsParameter(finalParamPath, '/')){
							paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText.getToolTipText());
					    	finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText.getToolTipText(), paramValue, extSchemaPathText);
				    		}
						
						if(finalParamPath.endsWith("/")){
							if(showErrorMessage){
							WidgetUtility.createMessageBox(NO_SCHEMA_NAME,"Error",SWT.ICON_ERROR|SWT.OK);
							 logger.error(NO_SCHEMA_NAME);
							}
							 return null;
						}
						else {
							schemaPath = getFilePath(defaultExtension, finalParamPath, fileExtensions);
						}
					  
				 }
			else {
				if (extSchemaPathText.getText().endsWith("/")) {
					if(showErrorMessage){
					WidgetUtility.createMessageBox(NO_SCHEMA_NAME, "Error",
							SWT.ICON_ERROR | SWT.OK);
					logger.error(NO_SCHEMA_NAME);
					}
					return null;
				}else{
					if(!checkEndsWith(extSchemaPathText.getText(), fileExtensions))
					{
							String textBoxPathWithExtension=extSchemaPathText.getText().concat(defaultExtension);
							extSchemaPathText.setText(textBoxPathWithExtension);
					}
					schemaPath = extSchemaPathText.getText();
				}
			}
				 if(!StringUtils.isEmpty(schemaPath) && !ParameterUtil.containsParameter(schemaPath, Path.SEPARATOR) &&!new File(schemaPath).isAbsolute()){
					 IWorkspace workspace = ResourcesPlugin.getWorkspace();
					 IPath relativePath=null;
					 try{
						 relativePath=workspace.getRoot().getFile(new Path(schemaPath)).getLocation();
					 }
					 catch(IllegalArgumentException e)
					 {
						 if(showErrorMessage){
						 WidgetUtility.createMessageBox(COULD_NOT_LOCATE_THE_EXTERNAL_SCHEMA_FILE_PATH,"Error",SWT.ICON_ERROR|SWT.OK);
						 logger.error(COULD_NOT_LOCATE_THE_EXTERNAL_SCHEMA_FILE_PATH,e);
						 }
						 return null;
					 }	
					 if(relativePath!=null){
							 schemaFile = new File(getFilePath(defaultExtension, relativePath.toOSString(),fileExtensions));
					 }
					 else{
						 schemaFile = new File(getFilePath(defaultExtension, schemaPath,fileExtensions));
					 }
				 }
				 else
				 {	
					 schemaFile = new File(getFilePath(defaultExtension, schemaPath,fileExtensions));
				 }
			 }
			 else{
				 if(ParameterUtil.containsParameter(extSchemaPathText.getText(), '/')){
					 String paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText.getText());
					 finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText.getText(), paramValue, extSchemaPathText);
						while(ParameterUtil.containsParameter(finalParamPath, '/')){
							paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText.getToolTipText());
					    	finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText.getToolTipText(), paramValue, extSchemaPathText);
				    		}
					  schemaPath = finalParamPath;
				 }
				 else{
					  schemaPath = extSchemaPathText.getText();
				 }
				 if(!new File(schemaPath).isAbsolute()){
					if(showErrorMessage){
					 Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
							 Messages.EXISTING_JOB_IS_NOT_SAVED_STATUS, null);
					 StatusManager.getManager().handle(status, StatusManager.BLOCK);
					 }
					 return schemaFile;
				 }
				 else {
					 schemaFile=new File(getFilePath(defaultExtension, schemaPath, fileExtensions));
				 }
			 }
			 return schemaFile;
		 }
		
	private static String getFilePath(String defaultExtension, String finalParamPath, String[] fileExtensions) {
		if (!checkEndsWith(finalParamPath, fileExtensions)) {
			return finalParamPath.concat(defaultExtension);
		} else {
			return finalParamPath;
		}
	}

	private static boolean checkEndsWith(String finalParamPath, String[] fileExtensions) {
		for(String extension:fileExtensions){
			if(StringUtils.endsWithIgnoreCase(finalParamPath, extension)){
				return true;
			}
		}
		return false;
	}

	 private void addImportExportButtons(Composite containerControl) {
		 ELTDefaultSubgroupComposite importExportComposite = new ELTDefaultSubgroupComposite(containerControl);
		 importExportComposite.createContainerWidget();
		 importExportComposite.numberOfBasicWidgets(3);
		 importExportComposite.getContainerControl().setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
		 ELTDefaultLable defaultLable = new ELTDefaultLable("");
		 defaultLable.lableWidth(0);
		 importExportComposite.attachWidget(defaultLable);
		 ELTDefaultButton importButtonWidget= new ELTDefaultButton(Messages.IMPORT_XML);
		 importExportComposite.attachWidget(importButtonWidget);
		 importButton = (Button) importButtonWidget.getSWTWidgetControl();
		 importButton.setEnabled(false);

		 importButton.addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 File schemaFile=getPath(extSchemaPathText,SCHEMA_FILE_EXTENSION,true, SCHEMA_FILE_EXTENSION,XML_FILE_EXTENSION);
				 if (schemaFile == null){
					 return;
				 }
				 loadSchemaIntoTable(schemaFile);
			 }

		 });

		 ELTDefaultButton exportButtonWidget = new ELTDefaultButton(Messages.EXPORT_XML).grabExcessHorizontalSpace(false);
		 if(OSValidator.isMac()){
			 exportButtonWidget.buttonWidth(100);
			 importButtonWidget.buttonWidth(100);
		 }
		 importExportComposite.attachWidget(exportButtonWidget);
		 exportButton = (Button) exportButtonWidget.getSWTWidgetControl();
		 exportButton.setEnabled(false);

		 //Add listener
		 exportButton.addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 if(!isSchemaValid)
				 {
					 if(WidgetUtility.createMessageBox
							 (Messages.SCHEMA_IS_INVALID_DO_YOU_WISH_TO_CONTINUE,Messages.EXPORT_SCHEMA,SWT.ICON_QUESTION | SWT.YES|SWT.NO)==SWT.YES)
					 {
						 exportSchemaToXmlFile();
					 }	
				 }	
				 else
				 {
					 exportSchemaToXmlFile();
				 }	
			 }
		 });
	 }
	 private void exportSchemaToXmlFile() {
		 File schemaFile=getPath(extSchemaPathText,SCHEMA_FILE_EXTENSION, true, SCHEMA_FILE_EXTENSION,XML_FILE_EXTENSION);
		 if (schemaFile == null){
			 return;
		 }
		 exportSchema(schemaFile);
	 }

	private void exportSchema(File schemaFile) {
		GridRowLoader gridRowLoader = new GridRowLoader(gridRowType, schemaFile);
		gridRowLoader.exportXMLfromGridRows(schemaGridRowList);
	}
	 
	private void loadSchemaIntoTable(File schemaFile) {
		List<GridRow> schemaGridRowListToImport = new ArrayList<GridRow>();

		tableViewer.setInput(schemaGridRowListToImport);
		tableViewer.refresh();

		GridRowLoader gridRowLoader = new GridRowLoader(gridRowType, schemaFile);

		schemaGridRowListToImport = gridRowLoader.importGridRowsFromXML(helper,tableViewer.getTable());

		if (schemaGridRowListToImport != null) {

			tableViewer.setInput(schemaGridRowList);
			tableViewer.refresh();
			enableDisableButtons(schemaGridRowListToImport.size());
			GridRowLoader.showMessageBox(Messages.IMPORTED_SCHEMA, "Information", SWT.ICON_INFORMATION);
			showHideErrorSymbol(applySchemaValidationRule());
		}
	}

	 // Adds the Radio buttons
	 private void createSchemaTypesSection(Composite containerControl) {
		 ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(containerControl);
		 eltSuDefaultSubgroupComposite.createContainerWidget();
		 eltSuDefaultSubgroupComposite.numberOfBasicWidgets(3);

		 AbstractELTWidget eltDefaultLable = new ELTDefaultLable(Messages.SCHEMA_TYPES).lableWidth(91);
		 eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);

		 // Radio button listener
		 internalSchema = new ELTRadioButton(Messages.INTERNAL_SCHEMA_TYPE);
		 eltSuDefaultSubgroupComposite.attachWidget(internalSchema);
		 ((Button) internalSchema.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 propertyDialogButtonBar.enableApplyButton(true);
				 toggleSchema(false);
				 external = false;
				 decorator.hide();
				 txtDecorator.hide();
			 }
		 });

		 externalSchema = new ELTRadioButton(Messages.EXTERNAL_SCHEMA_TYPE);
		 eltSuDefaultSubgroupComposite.attachWidget(externalSchema);
		 ((Button) externalSchema.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 propertyDialogButtonBar.enableApplyButton(true);
				 toggleSchema(true);
				 external = true;

				 if (StringUtils.isBlank(extSchemaPathText.getText())) {
					 importButton.setEnabled(false);
					 exportButton.setEnabled(false);
					 decorator.show();
				 } else {
					 decorator.hide();
				 }
			 }
		 });

		 populateSchemaTypeWidget();
	 }

	 private void gridListener(CellEditor[] cellEditors) {

		 GridChangeListener gridChangeListener = new GridChangeListener(cellEditors, propertyDialogButtonBar);
		 gridChangeListener.attachCellChangeListener();
	 }

	 private void populateWidget() {
		 if (this.properties != null) {
			 Schema schema = (Schema) this.properties;

			 if (!schema.getIsExternal()) {
				 if (tableViewer != null) {
					 schemaGridRowList = schema.getGridRow();
					 tableViewer.setInput(schemaGridRowList);
					 tableViewer.refresh();
					 external = false;
					 toggleSchema(false);
				 }
			 }
			 enableDisableButtons(schemaGridRowList.size());
		 }
	 }


	 private void populateWidgetExternalSchema() {
		 if (this.properties != null) {
			 Schema schema = (Schema) this.properties;
			 if (schema.getIsExternal()) {
				 if (extSchemaPathText != null) {
					 extSchemaPathText.setText(schema.getExternalSchemaPath());
					 schemaGridRowList = schema.getGridRow();
					 tableViewer.setInput(schemaGridRowList);
					 tableViewer.refresh();
					 decorator.hide();
					 external = true;
					 toggleSchema(true);
					 Utils.INSTANCE.addMouseMoveListener(extSchemaPathText, cursor);
				 }
			 } else {
				 toggleSchema(false);
			 }
			 enableDisableButtons(schemaGridRowList.size());
		 }
	 }

	 protected void populateWidgetExternalSchema(Object properties) {
		 if (properties != null) {
			 Schema schema = (Schema) properties;
			 this.properties=properties;
			 if (schema.getIsExternal()) {
				 if (extSchemaPathText != null) {
					 extSchemaPathText.setText(schema.getExternalSchemaPath());
					 schemaGridRowList = schema.getGridRow();
					 tableViewer.setInput(schemaGridRowList);
					 tableViewer.refresh();
					 decorator.hide();
					 external = true;
					 toggleSchema(true);
					 Utils.INSTANCE.addMouseMoveListener(extSchemaPathText, cursor);
				 }
			 } else {
				 toggleSchema(false);
			 }
			 enableDisableButtons(schemaGridRowList.size());
		 }
	 }

	 private void populateSchemaTypeWidget() {
		 if (this.properties != null) {
			 Schema schema = (Schema) this.properties;
			 if (schema.getIsExternal()) {
				 toggleSchemaChoice(true);
				 toggleSchema(true);
			 } else {
				 toggleSchemaChoice(false);
				 toggleSchema(false);
			 }
		 } else {
			 toggleSchemaChoice(false);
			 toggleSchema(false);
		 }
	 }


	 private void toggleSchemaChoice(boolean enableExternalSchemaRadioButton) {
		 if(externalSchema!=null){
			 ((Button) externalSchema.getSWTWidgetControl()).setSelection(enableExternalSchemaRadioButton);
			 ((Button) internalSchema.getSWTWidgetControl()).setSelection(!enableExternalSchemaRadioButton);
		 }
	 }

	 private void toggleSchema(boolean enableExtSchema) {
		 if (extSchemaPathText!=null && browseButton != null) {
			 extSchemaPathText.setEnabled(enableExtSchema);
			 browseButton.setEnabled(enableExtSchema);
			 if(StringUtils.isNotBlank(extSchemaPathText.getText()))
			 {	
				 importButton.setEnabled(enableExtSchema);
				 exportButton.setEnabled(enableExtSchema);
			 }
		 }
	 }

	 public ListenerHelper getListenerHelper() {
		 if (helper == null) {
			 helper = new ListenerHelper();
			 if (this.properties != null) {
				 Schema schema = (Schema) this.properties;
				 schemaGridRowList = schema.getGridRow();
			 }
			 ELTGridDetails value = new ELTGridDetails(schemaGridRowList, tableViewer,(Label) fieldError.getSWTWidgetControl(), gridWidgetBuilder);
			 helper.put(HelperType.SCHEMA_GRID, value);
			 helper.put(HelperType.COMPONENT_TYPE,componentType);
			 helper.put(HelperType.COMPONENT, getComponent());
		 }
		 return helper;
	 }

	 /**
	  * 
	  * Creates schema grid section
	  * 
	  * @param {@link Composite}
	  * @return {@link TableViewer}
	  */
	 public TableViewer createSchemaGridSection(Composite container, int height,
			 int width) {

		 ELTSchemaTableComposite gridSubGroup = new ELTSchemaTableComposite(
				 container);
		 gridSubGroup.createContainerWidget();

		 //compositeOfOutsideTable = new ColumnLayoutData();
		 compositeOfOutsideTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		 compositeOfOutsideTable.heightHint = 260;
		 compositeOfOutsideTable.widthHint = 400;
		 gridSubGroup.getContainerControl().setLayoutData(compositeOfOutsideTable);

		 Composite composite = new Composite(gridSubGroup.getContainerControl(),
				 SWT.NONE);
		 composite.setLayout(new GridLayout(1, false));
		 GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				 1);

		 composite.setLayoutData(gd_composite);

		 tableViewerComposite = new Composite(composite, SWT.BORDER);
		 GridLayout gd_layoutTable=(new GridLayout(1,false));
		 gd_layoutTable.marginHeight=0;
		 gd_layoutTable.marginWidth=0;
		 tableViewerComposite.setLayout(gd_layoutTable);
		 GridData gd_tableViewerComposite = new GridData(SWT.FILL, SWT.FILL, true,
				 true, 1, 1);
		 tableViewerComposite.setLayoutData(gd_tableViewerComposite);


		 tableComposite = new Composite(tableViewerComposite, SWT.NONE);
		 GridLayout gl_composite_2 = new GridLayout(1, false);
		 gl_composite_2.marginWidth = 0;
		 gl_composite_2.marginHeight = 0;
		 gl_composite_2.horizontalSpacing = 0;
		 tableComposite.setLayout(gl_composite_2);
		
		 tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				 1, 1));

		 AbstractELTWidget eltTableViewer = new ELTTableViewer(
				 getContentProvider(), getLableProvider());
		 eltTableViewer.attachWidget(tableComposite);

		 tableViewer = (TableViewer) eltTableViewer.getJfaceWidgetControl();
		 tableViewer.setInput(schemaGridRowList);


		 addGridRowsCopyPasteContextMenu();

		 // Set the editors, cell modifier, and column properties
		 tableViewer.setColumnProperties(PROPS);
		 tableViewer.setCellModifier(getCellModifier());
		 ELTTable eltTable = new ELTTable(tableViewer, height, width);
		 gridSubGroup.attachWidget(eltTable);

		 table = (Table) eltTable.getSWTWidgetControl();

		 // Create Table column
		 WidgetUtility.createTableColumns(table, PROPS);
		 // Set up the table
		 for (int columnIndex = 0, n = table.getColumnCount(); columnIndex < n; columnIndex++) {
			 table.getColumn(columnIndex).pack();
			 table.getColumn(columnIndex).setWidth(COLUMN_WIDTH);
		 }
		 editors = gridWidgetBuilder.createCellEditorList(table, columns);
		 tableViewer.setCellEditors(editors);
         
		 // enables the tab functionality
		 TableViewerEditor.create(tableViewer,
				 new ColumnViewerEditorActivationStrategy(tableViewer),
				 ColumnViewerEditor.KEYBOARD_ACTIVATION
				 | ColumnViewerEditor.TABBING_HORIZONTAL
				 | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				 | ColumnViewerEditor.TABBING_VERTICAL);

		 if(StringUtils.equalsIgnoreCase(getComponent().getCategory(), Constants.STRAIGHTPULL)
				 || (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.FILTER_COMPONENT)
				 || (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.UNIQUE_SEQUENCE_COMPONENT))
				 || (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.PARTITION_BY_EXPRESSION)))
			 )
		 
		 {	
			 //table.setEnabled(false);
			 for(CellEditor cellEditor:editors)
			 {
				 tableViewerComposite.setToolTipText("Non-editable");
				 cellEditor.getControl().setEnabled(false);
			 }
		} 
		 helper = getListenerHelper();

		 // Adding the decorator to show error message when field name same.
		 setDecorator();

		 addValidators();
		 if(addButton!=null)
		 {
		 try {
			 
			 eltTable.attachListener(
					 ListenerFactory.Listners.GRID_MOUSE_DOUBLE_CLICK
					 .getListener(), propertyDialogButtonBar, helper,
					 table, deleteButton.getSWTWidgetControl(), upButton
					 .getSWTWidgetControl(), downButton
					 .getSWTWidgetControl());
			 eltTable.attachListener(ListenerFactory.Listners.MOUSE_HOVER_LISTENER
					 .getListener(), propertyDialogButtonBar, helper, table);

			 eltTable.attachListener(ListenerFactory.Listners.MOUSE_MOVE_LISTENER
					 .getListener(), propertyDialogButtonBar, helper, table);
			 eltTable.attachListener(ListenerFactory.Listners.DISPOSE_LISTENER
					 .getListener(), propertyDialogButtonBar, helper, table);

			 eltTable.attachListener(ListenerFactory.Listners.KEY_DOWN_LISTENER
					 .getListener(), propertyDialogButtonBar, helper, table);

			 eltTable.attachListener(
					 ListenerFactory.Listners.GRID_MOUSE_DOWN.getListener(),
					 propertyDialogButtonBar, helper, editors[0].getControl());

			 eltTable.attachListener(
					 ListenerFactory.Listners.GRID_KEY_LISTENER.getListener(),
					 propertyDialogButtonBar, helper, table,
					 deleteButton.getSWTWidgetControl(),
					 upButton.getSWTWidgetControl(),
					 downButton.getSWTWidgetControl());

			 addButton.attachListener(
					 ListenerFactory.Listners.GRID_ADD_SELECTION.getListener(),
					 propertyDialogButtonBar, helper, table,
					 deleteButton.getSWTWidgetControl(),
					 upButton.getSWTWidgetControl(),
					 downButton.getSWTWidgetControl());

			 deleteButton.attachListener(
					 ListenerFactory.Listners.GRID_DELETE_SELECTION
					 .getListener(), propertyDialogButtonBar, helper,
					 table, deleteButton.getSWTWidgetControl(), upButton
					 .getSWTWidgetControl(), downButton
					 .getSWTWidgetControl());

			 for (CellEditor editor:editors){
				 addShortcutKeyListener(editor.getControl());
			 }
			 addShortcutKeyListener(table);
		 
		 
		 } catch (Exception e) {
			 logger.error(Messages.ATTACH_LISTENER_ERROR, e);
			 throw new RuntimeException(Messages.ATTACH_LISTENER_ERROR);
		 
		 }

		 gridListener(editors);
		 upButton.setEnabled(false);
		 downButton.setEnabled(false);
		 deleteButton.setEnabled(false);
		 }
		 TableViewerEditor.create(tableViewer, new ColumnViewerEditorActivationStrategy(tableViewer),
				 ColumnViewerEditor.KEYBOARD_ACTIVATION
				 | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				 | ColumnViewerEditor.TABBING_VERTICAL
				 | ColumnViewerEditor.TABBING_HORIZONTAL);

		 tableViewer.getControl().addKeyListener(new KeyListener() {

			 @Override
			 public void keyReleased(KeyEvent e) {
				 // Do - Nothing
			 }

			 @Override
			 public void keyPressed(KeyEvent e) {
				 if (e.keyCode == SWT.F2) {
					 if (tableViewer.getSelection() != null) {
						 StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
						 if (selection.size() == 1) {
							 GridRow gridRow = (GridRow) selection.getFirstElement();
							 int index = schemaGridRowList.indexOf(gridRow);
							 if (index > -1) {
								 tableViewer.editElement(tableViewer.getElementAt(index), 0);
							 }
						 }
					 }
				 }
			 }
		 });
		 
		 populateWidget();

		 arrangeTableViewerColumns();

		 return tableViewer;
	 }

	 /**
	  * Arrange the columns size in equal ratio
	  */
	 private void arrangeTableViewerColumns() {
		 tableViewerComposite.addControlListener(new ControlAdapter() {

			 @Override
			 public void controlResized(ControlEvent e) {
				 int totalWidth = tableViewer.getTable().getColumnCount() * COLUMN_WIDTH;
				 int widthDifference = tableViewerComposite.getSize().x - totalWidth;

				 if (widthDifference > 0) {
					 widthDifference = widthDifference / tableViewer.getTable().getColumnCount();
					 for (TableColumn tableColumn : tableViewer.getTable().getColumns()) {
						 tableColumn.setWidth(COLUMN_WIDTH+widthDifference);
					 }
				 }
			 }

		 });
	 }	



	 private void addGridRowsCopyPasteContextMenu() {
		 Menu menu = new Menu(tableViewer.getControl());
		 copyMenuItem = new MenuItem(menu, SWT.PUSH);
		 copyMenuItem.setText(Messages.COPY_MENU_TEXT);
		 copyMenuItem.setAccelerator(SWT.CTRL + 'C');
		 copyMenuItem.addSelectionListener(new SelectionListener() {

			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 logger.trace("Copying gridRows");
				 copiedGridRows.clear();
				 for (TableItem tableItem:tableViewer.getTable().getSelection()){
					 copiedGridRows.add((GridRow) tableItem.getData());
					 logger.trace("Copied", ((GridRow) tableItem.getData()).getFieldName());
				 }
				 pasteMenuItem.setEnabled(true);
			 }

			 @Override
			 public void widgetDefaultSelected(SelectionEvent e) {

			 }
		 });

		 pasteMenuItem = new MenuItem(menu, SWT.PUSH);
		 pasteMenuItem.setText(Messages.PASTE_MENU_TEXT);
		 pasteMenuItem.setAccelerator(SWT.CTRL + 'V');
		 pasteMenuItem.setEnabled(!copiedGridRows.isEmpty());

		 pasteMenuItem.addSelectionListener(new SelectionListener() {

			 @Override
			 public void widgetSelected(SelectionEvent e) {
				 logger.trace("Pasting gridRows");
				 ELTGridDetails eltGridDetails = (ELTGridDetails)helper.get(HelperType.SCHEMA_GRID);
				 for (GridRow copiedRow:copiedGridRows){
					 logger.trace("Pasted",copiedRow.getFieldName());
					 GridRow pasteGrid = copiedRow.copy();

					 int copyCount =0;	
					 do{
						 pasteGrid.setFieldName(copiedRow.getFieldName() + Messages.COPY_GRID_SUFFIX + copyCount++);
					 }while(eltGridDetails.getGrids().contains(pasteGrid));

					 eltGridDetails.getGrids().add(pasteGrid);
				 }
				 tableViewer.setInput(eltGridDetails.getGrids());
				 tableViewer.refresh();
			 }

			 @Override
			 public void widgetDefaultSelected(SelectionEvent e) {

			 }
		 });

		 tableViewer.getTable().setMenu(menu);
	 }
	 
	private void addImportSchemaButton(ELTSchemaSubgroupComposite buttonSubGroup) {

		importSchemaButton = new ELTDefaultButton("");
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(importSchemaButton, macButtonWidth, macButtonHeight,
				windowButtonWidth, windowButtonHeight);
		buttonSubGroup.attachWidget(importSchemaButton);
		importSchemaButton.setImage(ImagePathConstant.IMPORT_SCHEMA_BUTTON);
		importSchemaButton.setToolTipText(Messages.IMPORT_SCHEMA_KEY_SHORTCUT_TOOLTIP);
		Button importButton = (Button) importSchemaButton.getSWTWidgetControl();
		importButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				importSchema(importButton);
			}
		});
	}
	

	private void importSchema(Button importButton) {
		GenericImportExportFileDialog importFileDialog = new GenericImportExportFileDialog(
				importButton.getShell(), SWT.OPEN);
		importFileDialog.setFileName(StringUtils.EMPTY);
		importFileDialog.setTitle(Messages.IMPORT_SCHEMA_DIALOG_TITLE);
		importFileDialog.setFilterNames(new String[] { IMPORT_SCHEMA_FILE_EXTENSION_NAME });
		importFileDialog.setFilterExtensions(new String[] { IMPORT_SCHEMA_FILE_EXTENSION_FILTER });

		String filePath = importFileDialog.open();
		if (StringUtils.isNotBlank(filePath)) {
			
			 File schemaFile = new File(filePath);
			 if (schemaFile == null || !schemaFile.exists()){
				 return;
			 }
			 loadSchemaIntoTable(schemaFile);
			 setSchemaUpdated(true);
			 propertyDialogButtonBar.enableApplyButton(true);
		}
	}

	private void addExportSchemaButton(ELTSchemaSubgroupComposite buttonSubGroup) {

		exportSchemaButton = new ELTDefaultButton("");
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(exportSchemaButton, macButtonWidth, macButtonHeight,
				windowButtonWidth, windowButtonHeight);
		buttonSubGroup.attachWidget(exportSchemaButton);
		exportSchemaButton.setImage(ImagePathConstant.EXPORT_SCHEMA_BUTTON);
		exportSchemaButton.setToolTipText(Messages.EXPORT_SCHEMA_KEY_SHORTCUT_TOOLTIP);
		Button exportButton = (Button) exportSchemaButton.getSWTWidgetControl();
		exportButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				exportSchemaIntoFile(exportButton);
			}
		});
	}
	
	private void exportSchemaIntoFile(Button exportButton) {
		GenericImportExportFileDialog exportFileDialog = new GenericImportExportFileDialog(
				exportButton.getShell(), SWT.SAVE);
		exportFileDialog.setTitle(Messages.EXPORT_SCHEMA_DIALOG_TITLE);
		exportFileDialog.setFilterExtensions(new String[] { EXPORT_XML_FILE_EXTENSION_FILTER, EXPORT_SCHEMA_FILE_EXTENSION_FILTER });

		String filePath = exportFileDialog.open();
		if (StringUtils.isNotBlank(filePath)) {

			File schemaFile = new File(filePath);
			if (schemaFile != null) {
				if (!isSchemaValid) {
					if (WidgetUtility.createMessageBox(Messages.SCHEMA_IS_INVALID_DO_YOU_WISH_TO_CONTINUE,
							Messages.EXPORT_SCHEMA, SWT.ICON_QUESTION | SWT.YES | SWT.NO) == SWT.YES) {
						exportSchema(schemaFile);
					}
				} else {
					exportSchema(schemaFile);
				}
			}
		}
	}

	 private void addAddButton(ELTSchemaSubgroupComposite buttonSubGroup) {

		 addButton = new ELTDefaultButton("");

		 SchemaButtonsSyncUtility.INSTANCE.buttonSize(addButton, macButtonWidth, macButtonHeight, windowButtonWidth, windowButtonHeight);
		 buttonSubGroup.attachWidget(addButton);
		 addButton.setImage(ImagePathConstant.ADD_BUTTON);
		 addButton.setToolTipText(Messages.ADD_KEY_SHORTCUT_TOOLTIP);
	 }

	 private void addDeleteButton(ELTSchemaSubgroupComposite buttonSubGroup) {
		 deleteButton = new ELTDefaultButton("");
		 SchemaButtonsSyncUtility.INSTANCE.buttonSize(deleteButton, macButtonWidth, macButtonHeight, windowButtonWidth, windowButtonHeight);
		 buttonSubGroup.attachWidget(deleteButton);
		 deleteButton.setImage(ImagePathConstant.DELETE_BUTTON);
		 deleteButton.setToolTipText(Messages.DELETE_KEY_SHORTCUT_TOOLTIP);
	 }

	 private void addUpButton(ELTSchemaSubgroupComposite buttonSubGroup) {
		 upButton = new ELTDefaultButton("");
		 SchemaButtonsSyncUtility.INSTANCE.buttonSize(upButton, macButtonWidth, macButtonHeight, windowButtonWidth, windowButtonHeight);
		 buttonSubGroup.attachWidget(upButton);
		 upButton.setImage(ImagePathConstant.MOVEUP_BUTTON);
		 upButton.setToolTipText(Messages.MOVE_UP_KEY_SHORTCUT_TOOLTIP);
		 ((Button)upButton.getSWTWidgetControl()).addMouseListener(new MouseAdapter() {

			 @Override
			 public void mouseDown(MouseEvent e) {
				 moveRowUp();
			 }


		 });


	 }

	 private void moveRowUp()
	 {
		 propertyDialogButtonBar.enableApplyButton(true);
		 int[] indexes=table.getSelectionIndices();
		 for(int index :indexes)
		 {

			 if (index > 0) {
				 Collections.swap(schemaGridRowList,index ,index-1);
				 tableViewer.refresh();

			 }
		 }	
	 }

	 private void addDownButton(ELTSchemaSubgroupComposite buttonSubGroup) {
		 downButton = new ELTDefaultButton("");
		 SchemaButtonsSyncUtility.INSTANCE.buttonSize(downButton, macButtonWidth, macButtonHeight, windowButtonWidth, windowButtonHeight);
		 buttonSubGroup.attachWidget(downButton);

		 downButton.setImage(ImagePathConstant.MOVEDOWN_BUTTON);
		 downButton.setToolTipText(Messages.MOVE_DOWN_KEY_SHORTCUT_TOOLTIP);

		 ((Button)downButton.getSWTWidgetControl()).addMouseListener(new MouseAdapter() {

			 @Override
			 public void mouseDown(MouseEvent e) {
				 moveRowDown();
			 }
		 });


	 }

	 private void moveRowDown()
	 {
		 propertyDialogButtonBar.enableApplyButton(true);
		 int[] indexes = table.getSelectionIndices();
		 for (int i = indexes.length - 1; i > -1; i--) {

			 if (indexes[i] < schemaGridRowList.size() - 1) {
				 Collections.swap(schemaGridRowList,indexes[i] ,indexes[i]+1);
				 tableViewer.refresh();

			 }

		 }

	 }

	 public List<GridRow> getSchemaGridRowList() {
		 return schemaGridRowList;
	 }

	 public void setSchemaGridRowList(List<GridRow> schemaGridRowList) {
		 this.schemaGridRowList = schemaGridRowList;
	 } 
	 /**
	  * Called on tab switch, Its use to propagate internal schema from source to target.
	  * propagation is restricted for components having pull schema feature. 
	  */
	 @Override
	 public void refresh() {

		 Schema schema = getSchemaForInternalPropagation();

		 {
			  if(!SchemaSyncUtility.INSTANCE.isSchemaSyncAllow( getComponent().getComponentName())){

				 updateSchemaTableViewer(schema);
			 }
		 }
		 if(refreshIfSetAllPassthroughForTransformIsChecked(getComponent())){
			 updateSchemaTableViewer(schema);
		 }
		 showHideErrorSymbol(isWidgetValid());
		 SchemaRowValidation.INSTANCE.highlightInvalidRowWithRedColor(null, null,table,componentType);
	 }

	private void updateSchemaTableViewer(Schema schema) {
		if (schema.getGridRow().size() != 0) {
			 table.clearAll();
			 if (!schema.getIsExternal()) {
				 if (tableViewer != null) {
					 schemaGridRowList = new ArrayList<>(schema.getGridRow());
					 ELTGridDetails eLTDetails= (ELTGridDetails) helper.get(HelperType.SCHEMA_GRID);
					 eLTDetails.setGrids(schemaGridRowList); 
					 tableViewer.setInput(schemaGridRowList);
					 tableViewer.refresh();
					 external = false;
					 toggleSchema(false);
				 }
			 }
		 }
	}

	 private boolean refreshIfSetAllPassthroughForTransformIsChecked(Component component) {
		 String componentName=component.getComponentName();
		 TransformMapping transformMapping=(TransformMapping)component.getProperties().get(Constants.OPERATION);
          if(transformMapping!=null && ( StringUtils.equalsIgnoreCase(Constants.TRANSFORM, componentName) || 
   			   StringUtils.equalsIgnoreCase(Constants.AGGREGATE, componentName) ||
   			   StringUtils.equalsIgnoreCase(Constants.NORMALIZE, componentName) ||
   			   StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE, componentName) ||
   			   StringUtils.equalsIgnoreCase(Constants.CUMULATE, componentName))){
        	  return transformMapping.isAllInputFieldsArePassthrough();
          }
          return false;
	}

	public boolean isTransformSchemaType() {
		 return transformSchemaType;
	 }

	 public void setTransformSchemaType(boolean isTransformSchemaType) {
		 this.transformSchemaType = isTransformSchemaType;
	 }

	 public void enableDisableButtons(int size) {
		 if(deleteButton!=null)
		 {	 
		 if (size >= 1) {
			 deleteButton.setEnabled(true);
		 } else {
			 deleteButton.setEnabled(false);
		 }
		 if (size >= 2) {
			 upButton.setEnabled(true);
			 downButton.setEnabled(true);
		 } else {
			 upButton.setEnabled(false);
			 downButton.setEnabled(false);
		 }
		 }
	 }

	 public boolean isExternal() {
		 return external;
	 }
	 public int getSizeOfTableViewer()
	 {
		 return schemaGridRowList.size();
	 }

	 private void syncInternallyPropagatedSchema() {
		 Schema schema = getSchemaForInternalPropagation();
		if (getComponent().getCategory().equalsIgnoreCase(Constants.TRANSFORM_DISPLAYNAME)) {
			 List<GridRow> tempList = new ArrayList<>();
			 schemaGridRowList =(List<GridRow>) tableViewer.getInput();
			 tempList.addAll(propogateInternalSchemaForTransform(schemaGridRowList, schema.getGridRow()));
			 schemaGridRowList.clear();
			 schemaGridRowList.addAll(tempList);
			  /*remove duplicates*/
			//schemaGridRowList = schemaGridRowList.parallelStream().distinct().collect(Collectors.toList());
			 ELTGridDetails eLTDetails = (ELTGridDetails) helper
					 .get(HelperType.SCHEMA_GRID);
			 eLTDetails.setGrids(schemaGridRowList);
			 tableViewer.setInput(schemaGridRowList);
		 }else{
			 schemaGridRowList.clear();
			 schemaGridRowList.addAll(schema.getGridRow());
			 ELTGridDetails eLTDetails = (ELTGridDetails) helper
					 .get(HelperType.SCHEMA_GRID);
			 eLTDetails.setGrids(schemaGridRowList);
		 }

		 tableViewer.refresh();
	 }

	private void showMessage() {
		 MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION);
		 messageBox.setMessage(Messages.SCHEMA_IS_UPTO_DATE_MESSAGE);
		 messageBox.setText(Messages.PULL_SCHEMA_MESSAGE_TITLE);
		 messageBox.open();
	}

	 protected void attachListener() {
		 if (extSchemaPathText != null) {
			 extSchemaPathText.addModifyListener(new ModifyListener() {

				 @Override
				 public void modifyText(ModifyEvent e) {
					 Utils.INSTANCE.addMouseMoveListener(extSchemaPathText, cursor);

					 showHideErrorSymbol(isWidgetValid());

				 }
			 });

			 ((Button) externalSchema.getSWTWidgetControl())
			 .addSelectionListener(new SelectionAdapter() {
				 @Override
				 public void widgetSelected(SelectionEvent e) {
					 if (StringUtils.isBlank(extSchemaPathText.getText())) {
						 showHideErrorSymbol(isWidgetValid());
					 }
				 }

			 });
			 ((Button) internalSchema.getSWTWidgetControl())
			 .addSelectionListener(new SelectionAdapter() {
				 @Override
				 public void widgetSelected(SelectionEvent e) {
					 showHideErrorSymbol(isWidgetValid());

				 }

			 });

		 }
if(deleteButton!=null)
{	
		 ((Button)deleteButton.getSWTWidgetControl()).addSelectionListener(new SelectionListener() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {/*Do-Nothing*/}

			 @Override
			 public void widgetDefaultSelected(SelectionEvent e) {
				 if (table.getItemCount() == 0) {
					 showHideErrorSymbol(isWidgetValid());
				 }
			 }			

		 });


		 ((Button)addButton.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {
			 @Override
			 public void widgetSelected(SelectionEvent e) {
			 }
		 });
}
	 }

	 protected boolean applySchemaValidationRule() {

		 if (!schemaGridRowList.isEmpty()) {
			 Schema schema = new Schema();
			 schema.setGridRow(schemaGridRowList);
			 if (extSchemaPathText != null)
				 schema.setExternalSchemaPath(extSchemaPathText.getText());
			 schema.setIsExternal(external);
			 isSchemaValid=validateAgainstValidationRule(schema);
			 return isSchemaValid;
		 } else
		 {
			 isSchemaValid= validateAgainstValidationRule(getComponent().getProperties()
					 .get(Constants.SCHEMA_PROPERTY_NAME));
			 return isSchemaValid;
		 }
	 }

	 public List<NameValueProperty> getComponentSchemaAsProperty(){
		 List<NameValueProperty> outputFileds = new ArrayList<>();
		 for (GridRow gridRow : schemaGridRowList) {
			 NameValueProperty nameValueProperty = new NameValueProperty();
			 nameValueProperty.setPropertyName("");
			 nameValueProperty.setPropertyValue(gridRow.getFieldName());
			 outputFileds.add(nameValueProperty);
		 }
		 return outputFileds;
	 }


	 public List<FilterProperties> convertSchemaToFilterProperty(){
		 List<FilterProperties> outputFileds = new ArrayList<>();
		 for (GridRow gridRow : schemaGridRowList) {
			 FilterProperties filterProperty = new FilterProperties();
			 filterProperty.setPropertyname(gridRow.getFieldName());
			 outputFileds.add(filterProperty);
		 }
		 return outputFileds;
	 }


	 public TableViewer getTableViewer() {
		return tableViewer;
	}

	public Table getTable() {
		 return table;
	 }

	 public String getComponentType() {
		 return componentType;
	 }

	 private void addShortcutKeyListener (Control currentControl) {
		 logger.trace("currentControl is: " + currentControl);
		 currentControl.addKeyListener(new KeyListener() {						

			 @Override
			 public void keyReleased(KeyEvent event) {				
				 if(event.keyCode == SWT.CTRL || event.keyCode == SWT.COMMAND){					
					 ctrlKeyPressed = false;
				 }							
			 }

			 @Override
			 public void keyPressed(KeyEvent event) {
				 if(event.keyCode == SWT.CTRL || event.keyCode == SWT.COMMAND){					
					 ctrlKeyPressed = true;
				 }									

				 else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_UP){
					 logger.info("Key pressed is arrow up");
					 moveRowUp();			
				 }

				 else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_DOWN){
					 logger.info("Key pressed is arrow down");
					 moveRowDown();
				 }
			 }
		 });
	 }

	 @Override
	 public void refresh(Object value) {

		 Schema schema = (Schema)value;

		 {
			 if(!SchemaSyncUtility.INSTANCE.isSchemaSyncAllow( getComponent().getComponentName())){
				 updateSchemaTableViewer(schema);
			 }
		 }
		 SchemaRowValidation.INSTANCE.highlightInvalidRowWithRedColor(null, null,table,componentType);
		 showHideErrorSymbol(applySchemaValidationRule());
		 LinkedHashMap<String, Object> currentSchemaProperty = new LinkedHashMap<>();
		 currentSchemaProperty.put(propertyName, schema);
	 }
	 
	 
	 /**
		 * This Function calls to merge Transform component Dialog's outputField schema fields to schema tab schema than overwrite
		 * @param schemaGridRowList
		 * @param transformInternalSchema
		 * @return 
		 */
		private List<GridRow> propogateInternalSchemaForTransform(List<GridRow> schemaGridRowList, List<GridRow> transformInternalSchema){
			 List<GridRow> tempSchema = new ArrayList<>();
			 if(schemaGridRowList.isEmpty()){
				 tempSchema.addAll(transformInternalSchema);
			 }else{
				 tempSchema.addAll(compareInternalSchemaForTransForm(schemaGridRowList, transformInternalSchema));
			 }
			 
			return tempSchema;
		 }
		 
		 
		 /**
		  * The function will used to compare Transform component Dialog's outputField schema to schema tab schema 
		 * @param outputSchema
		 * @param internalSchema
		 * @return List of Schema Fields
		 */
		private List<GridRow> compareInternalSchemaForTransForm(List<GridRow> outputSchema, List<GridRow> internalSchema){
			List<GridRow> tempList = (List<GridRow>) ((ArrayList<GridRow>)outputSchema).clone();
			tempList.forEach(gridRow -> {if(!internalSchema.contains(gridRow)){ outputSchema.remove(gridRow);}});
			internalSchema.forEach(gridRow -> {if(!outputSchema.contains(gridRow)){outputSchema.add(gridRow);}});
			sequencingOfSchemaFieldsInOrderOfInternalSchemaFields(outputSchema,internalSchema);
			return outputSchema;
		 }
		
		private void sequencingOfSchemaFieldsInOrderOfInternalSchemaFields(List<GridRow> outputSchema,
				List<GridRow> internalSchema2) {
			for(int index=0;index<internalSchema2.size();index++){
				if(!StringUtils.equalsIgnoreCase(outputSchema.get(index).getFieldName(), internalSchema2.get(index).getFieldName())){
					outputSchema.set(index,internalSchema2.get(index));
				}
			}
		}
}
