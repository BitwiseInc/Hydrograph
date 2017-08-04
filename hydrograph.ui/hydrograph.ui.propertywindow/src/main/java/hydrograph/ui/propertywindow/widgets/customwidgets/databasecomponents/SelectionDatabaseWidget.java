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
package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.datastructures.property.database.DatabaseParameterType;
import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.DatabaseSelectionConfig;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.TextBoxWithLableConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.metastore.HiveTableSchema;
import hydrograph.ui.propertywindow.widgets.customwidgets.metastore.HiveTableSchemaField;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTRadioButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTSubGroupCompositeWithStack;
import hydrograph.ui.propertywindow.widgets.listeners.IELTListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * SelectionDatabaseWidget provides 
 * @author Bitwise
 *
 */
public class SelectionDatabaseWidget extends AbstractWidget {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(LoadTypeConfigurationWidget.class);
	private String propertyName;
	private DatabaseSelectionConfig databaseSelectionConfig;
	private ELTRadioButton tableNameRadioButton;
	private ELTRadioButton sqlQueryRadioButton;
	private TextBoxWithLableConfig textBoxConfig;
	private ControlDecoration tableNameDecorator;
	private ControlDecoration sqlQueryDecorator;
	private ELTSubGroupCompositeWithStack tableComposite;
	private ELTSubGroupCompositeWithStack sqlQueryComposite;
	private Text sqlQueryTextBox;
	private ArrayList<AbstractWidget> widgets;
	private Text textBoxTableName;
	private ELTDefaultLable selectLable;
	private ModifyListener textboxSQLQueryModifyListner;
	private ModifyListener textboxTableNameModifyListner;
	private Cursor cursor;
	private String sqlQueryStatement;
	private Text sqlQueryCountertextbox;
	private ModifyListener sqlQueryCounterModifyListner;
	private static final String ORACLE = "oracle";
	private static final String REDSHIFT = "redshift";
	private static final String MYSQL = "mysql";
	private static final String TERADATA = "teradata";
	
	private int key_value = 0;

	public SelectionDatabaseWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propertyDialogButtonBar) {

		super(componentConfigProp, componentMiscProps, propertyDialogButtonBar);
		this.propertyName = componentConfigProp.getPropertyName();
		this.databaseSelectionConfig = (DatabaseSelectionConfig) componentConfigProp.getPropertyValue();
		if(databaseSelectionConfig==null){
			databaseSelectionConfig=new DatabaseSelectionConfig();
		}
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		final ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				subGroup.getContainerControl());

		eltSuDefaultSubgroupComposite.createContainerWidget();
		eltSuDefaultSubgroupComposite.numberOfBasicWidgets(6);

		selectLable = new ELTDefaultLable(Messages.DATABASE_SELECT);
		eltSuDefaultSubgroupComposite.attachWidget(selectLable);
		(selectLable.getSWTWidgetControl()).setData(String.valueOf(key_value++), selectLable.getSWTWidgetControl());

		tableNameRadioButton = new ELTRadioButton(Messages.DATABASE_TABLE_NAME);
		eltSuDefaultSubgroupComposite.attachWidget(tableNameRadioButton);
		propertyDialogButtonBar.enableApplyButton(true);
		((Button) tableNameRadioButton.getSWTWidgetControl()).setSelection(true);
		
		AbstractELTWidget eltDefaultLable = new ELTDefaultLable("");
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);

		sqlQueryRadioButton = new ELTRadioButton(Messages.DATABASE_SQL_QUERY);
		eltSuDefaultSubgroupComposite.attachWidget(sqlQueryRadioButton);

		final ELTSubGroupCompositeWithStack selectionComposite = new ELTSubGroupCompositeWithStack(
				subGroup.getContainerControl());
		final StackLayout layout = new StackLayout();
		selectionComposite.createStackContainerWidget(layout);

		createSQLQueryComposite(selectionComposite);
		createTableNameComposite(selectionComposite);


		if (null != databaseSelectionConfig) {
			if (databaseSelectionConfig.isTableName()) {

				layout.topControl = tableComposite.getContainerControl();
				((Button) tableNameRadioButton.getSWTWidgetControl()).setSelection(true);

			} else {

				layout.topControl = sqlQueryComposite.getContainerControl();
				((Button) tableNameRadioButton.getSWTWidgetControl()).setSelection(false);
				((Button) sqlQueryRadioButton.getSWTWidgetControl()).setSelection(true);
			}
			eltSuDefaultSubgroupComposite.getContainerControl().layout();

		} else {

			layout.topControl = tableComposite.getContainerControl();
			eltSuDefaultSubgroupComposite.getContainerControl().layout();
			((Button) tableNameRadioButton.getSWTWidgetControl()).setSelection(true);

		}
		attachTableButtonListner(selectionComposite, layout);

		attachSQLQueryListner(selectionComposite, layout);
		populateWidget();

		setPropertyHelpWidget((Control) selectLable.getSWTWidgetControl());
	}

	/**
	 * 
	 * @param selectionComposite
	 * @param layout
	 */
	private void attachSQLQueryListner(final ELTSubGroupCompositeWithStack selectionComposite,
			final StackLayout layout) {
		final Button sqlRadioBtn = (Button) sqlQueryRadioButton.getSWTWidgetControl();
		sqlRadioBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (sqlRadioBtn.getSelection()) {
					unRegisterTableOrSQLQueryTextListner();
					layout.topControl = sqlQueryComposite.getContainerControl();
					selectionComposite.getContainerControl().layout();
					if (databaseSelectionConfig != null) {
						databaseSelectionConfig.setTableNameSelection(false);
						databaseSelectionConfig.setSqlQuery(sqlQueryTextBox.getText());
						//databaseSelectionConfig.setSqlQueryCounter(sqlQueryCountertextbox.getText());
						populateWidget();
					}
					showHideErrorSymbol(widgets);
					propertyDialogButtonBar.enableApplyButton(true);
				}
			}
		});
	}
	
	/**
	 * 
	 * @param selectionComposite
	 * @param layout
	 */
	private void attachTableButtonListner(final ELTSubGroupCompositeWithStack selectionComposite,
			final StackLayout layout) {
		final Button tableRadioBtn = (Button) tableNameRadioButton.getSWTWidgetControl();
		tableRadioBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tableRadioBtn.getSelection()) {
					unRegisterTableOrSQLQueryTextListner();
					layout.topControl = tableComposite.getContainerControl();
					selectionComposite.getContainerControl().layout();
					if (databaseSelectionConfig != null) {
						databaseSelectionConfig.setTableNameSelection(true);
						databaseSelectionConfig.setTableName(textBoxTableName.getText());
						populateWidget();
					}
					showHideErrorSymbol(widgets);
					propertyDialogButtonBar.enableApplyButton(true);
				}
			}
		});
	}

	/**
	 * Creates the stack layout composite for SQLQuery 
	 * @param selectionComposite
	 */
	private void createSQLQueryComposite(ELTSubGroupCompositeWithStack selectionComposite) {

		Utils.INSTANCE.loadProperties();
		cursor = selectionComposite.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		sqlQueryComposite = new ELTSubGroupCompositeWithStack(selectionComposite.getContainerControl());
		sqlQueryComposite.createContainerWidget();

		sqlQueryComposite.numberOfBasicWidgets(3);

		AbstractELTWidget createWidgetlabel = createWidgetlabel(Messages.SQL_QUERY, sqlQueryComposite);
		selectLable.getSWTWidgetControl().setData(String.valueOf(key_value++), createWidgetlabel.getSWTWidgetControl());

		AbstractELTWidget sqlQueryWgt = createWidgetTextbox(Messages.SQL_QUERY, sqlQueryComposite);
		sqlQueryDecorator = attachDecoratorToTextbox(Messages.SQL_QUERY, sqlQueryWgt, sqlQueryDecorator);
		sqlQueryTextBox = (Text) sqlQueryWgt.getSWTWidgetControl();
		attachListeners(sqlQueryWgt);

		ELTDefaultButton sqlQueryButtonWgt = new ELTDefaultButton("...");
		sqlQueryButtonWgt.buttonWidth(25);
		sqlQueryButtonWgt.buttonHeight(20);
		sqlQueryButtonWgt.grabExcessHorizontalSpace(false);
		sqlQueryComposite.attachWidget(sqlQueryButtonWgt);
		Button buttonAlignment = ((Button) sqlQueryButtonWgt.getSWTWidgetControl());
		GridData data = (GridData) buttonAlignment.getLayoutData();
		data.verticalIndent = 5;
		buttonAlignment.addSelectionListener(buttonWidgetSelectionListener(sqlQueryTextBox));
		buttonWidgetSelectionListener(sqlQueryTextBox);

		AbstractELTWidget createWidgetlabel2 = createWidgetlabel(Messages.EXTRACT_FROM_METASTORE, sqlQueryComposite);
		selectLable.getSWTWidgetControl().setData(String.valueOf(key_value++), createWidgetlabel2.getSWTWidgetControl());

		ELTDefaultButton editButton = new ELTDefaultButton(Messages.EXTRACT);
		sqlQueryComposite.attachWidget(editButton);

		Button button = (Button) editButton.getSWTWidgetControl();
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 0, 0);
		gridData.widthHint = 92;
		gridData.horizontalIndent = 15;
		button.setLayoutData(gridData);

		button.addSelectionListener(attachExtractButtonSelectionListner());
		//TODO Below code commented for future use
		/*
		 * Currently, we are not showing SQL_QUERY_COUNTER widget. So, below code will be comment out.
		 */
		/*createWidgetlabel(Messages.SQL_QUERY_COUNTER, sqlQueryComposite);
		AbstractELTWidget sqlQueryCounterWgt = createWidgetTextbox(Messages.SQL_QUERY_COUNTER, sqlQueryComposite);
		sqlQueryCountertextbox = (Text) sqlQueryCounterWgt.getSWTWidgetControl();
		attachListeners(sqlQueryCounterWgt);

		ELTDefaultButton sqlQueryCounterButtonWgt = new ELTDefaultButton("...");
		sqlQueryCounterButtonWgt.buttonWidth(25);
		sqlQueryCounterButtonWgt.buttonHeight(20);
		sqlQueryCounterButtonWgt.grabExcessHorizontalSpace(false);
		sqlQueryComposite.attachWidget(sqlQueryCounterButtonWgt);
		Button sqlQueryCounterButton = ((Button) sqlQueryCounterButtonWgt.getSWTWidgetControl());
		GridData sqlQueryCounterData = (GridData) sqlQueryCounterButton.getLayoutData();
		sqlQueryCounterData.verticalIndent = 5;
		sqlQueryCounterButton.addSelectionListener(buttonWidgetSelectionListener(sqlQueryCountertextbox));*/

	}

	private SelectionAdapter buttonWidgetSelectionListener(Text textWidget){
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = textWidget.getText();
				SQLQueryStatementDialog sqlQueryStatementDialog = new SQLQueryStatementDialog(
						Display.getCurrent().getActiveShell(), text);
				sqlQueryStatementDialog.open();
				if(sqlQueryStatementDialog.getStyleTextSqlQuery() != null){
					textWidget.setText(sqlQueryStatementDialog.getStyleTextSqlQuery());
				}
				if(sqlQueryStatementDialog.isTextValueChanged()){
					propertyDialogButtonBar.enableApplyButton(true);
				}
			}
		};
		return adapter;
	}
	

	@Override
	public LinkedHashMap<String, Object> getProperties() {

		LinkedHashMap<String, Object> property = new LinkedHashMap<>();
		DatabaseSelectionConfig databaseSelectionConfig = new DatabaseSelectionConfig();
		if (((Button) tableNameRadioButton.getSWTWidgetControl()).getSelection()) {
			databaseSelectionConfig.setTableName(textBoxTableName.getText());

		} else {
			databaseSelectionConfig.setTableNameSelection(false);
			databaseSelectionConfig.setSqlQuery(sqlQueryTextBox.getText());
			//TODO as above
			//databaseSelectionConfig.setSqlQueryCounter(sqlQueryCountertextbox.getText());
		}

		property.put(propertyName, databaseSelectionConfig);

		setToolTipErrorMessage();
		return property;
	}

	/**
	 * Unregisters all the modify listeners on TextBoxes
	 */
	protected void unRegisterTableOrSQLQueryTextListner() {
		if (((Button) tableNameRadioButton.getSWTWidgetControl()).getSelection()) {

			sqlQueryTextBox.removeModifyListener(textboxSQLQueryModifyListner);
			//TODO as above
			//sqlQueryCountertextbox.removeModifyListener(sqlQueryCounterModifyListner);
			registerTextBoxListner(true);

		} else {

			textBoxTableName.removeModifyListener(textboxTableNameModifyListner);
			registerTextBoxListner(false);

		}

	}
	
	/**
	 * Registers all the modify listeners on TextBoxes
	 * @param isTableNameRadioButton
	 */
	private void registerTextBoxListner(boolean isTableNameRadioButton) {

		if (isTableNameRadioButton) {
			textBoxTableName.addModifyListener(textboxTableNameModifyListner);
		} else {
			sqlQueryTextBox.addModifyListener(textboxSQLQueryModifyListner);
			//TODO as above
			//sqlQueryCountertextbox.addModifyListener(sqlQueryCounterModifyListner);
		}

	}

	/**
	 * Sets the data structure used for TextBoxes
	 */
	@Override
	public void setWidgetConfig(WidgetConfig widgetConfig) {
		textBoxConfig = (TextBoxWithLableConfig) widgetConfig;
	}

	/**
	 * Sets the tool tip error message
	 */
	protected void setToolTipErrorMessage() {

		String toolTipErrorMessage = null;

		if (sqlQueryDecorator.isVisible()) {
			toolTipErrorMessage = sqlQueryDecorator.getDescriptionText();
			setToolTipMessage(toolTipErrorMessage);
		}

		if (tableNameDecorator.isVisible()) {
			toolTipErrorMessage = tableNameDecorator.getDescriptionText();
			setToolTipMessage(toolTipErrorMessage);
		}

	}

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(databaseSelectionConfig);
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		widgets = widgetList;

		textboxTableNameModifyListner = attachTextModifyListner(widgetList);
		textboxSQLQueryModifyListner = attachTextModifyListner(widgetList);
		sqlQueryCounterModifyListner = attachTextModifyListner(widgetList);

		sqlQueryTextBox.addModifyListener(textboxSQLQueryModifyListner);
		textBoxTableName.addModifyListener(textboxTableNameModifyListner);
		//TODO as above
		//sqlQueryCountertextbox.addModifyListener(sqlQueryCounterModifyListner);
		
	}
	
	/**
	 * Applies multiple listeners to textBoxes 
	 * @param widgetList
	 * @return
	 */
	private ModifyListener attachTextModifyListner(final ArrayList<AbstractWidget> widgetList) {
		return new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				Text text = (Text)event.getSource();
				if(((Button) tableNameRadioButton.getSWTWidgetControl()).getSelection()){
					databaseSelectionConfig.setTableName(text.getText());
				}else{
					databaseSelectionConfig.setSqlQuery(text.getText());
				}
				Utils.INSTANCE.addMouseMoveListener(sqlQueryTextBox, cursor);
				Utils.INSTANCE.addMouseMoveListener(textBoxTableName, cursor);
				showHideErrorSymbol(widgetList);
			}
		};
	}
	
	/**
	 * Creates the stack layout composite for Table option
	 * @param eltSuDefaultSubgroupComposite
	 */
	private void createTableNameComposite(ELTSubGroupCompositeWithStack eltSuDefaultSubgroupComposite) {

		Utils.INSTANCE.loadProperties();
		cursor = eltSuDefaultSubgroupComposite.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		tableComposite = new ELTSubGroupCompositeWithStack(eltSuDefaultSubgroupComposite.getContainerControl());
		tableComposite.createContainerWidget();

		tableComposite.numberOfBasicWidgets(2);
		AbstractELTWidget createWidgetlabel = createWidgetlabel(Messages.LABEL_TABLE_NAME, tableComposite);
		selectLable.getSWTWidgetControl().setData(String.valueOf(key_value++), createWidgetlabel.getSWTWidgetControl());
		
		AbstractELTWidget tableNameWgt = createWidgetTextbox(Messages.LABEL_TABLE_NAME, tableComposite);
		tableNameDecorator = attachDecoratorToTextbox(Messages.LABEL_TABLE_NAME, tableNameWgt, tableNameDecorator);
		textBoxTableName = (Text) tableNameWgt.getSWTWidgetControl();

		attachListeners(tableNameWgt);

		AbstractELTWidget createWidgetlabel2 = createWidgetlabel(Messages.EXTRACT_FROM_METASTORE, tableComposite);
		selectLable.getSWTWidgetControl().setData(String.valueOf(key_value++), createWidgetlabel2.getSWTWidgetControl());
		ELTDefaultButton editButton = new ELTDefaultButton(Messages.EXTRACT);
		tableComposite.attachWidget(editButton);

		Button button = (Button) editButton.getSWTWidgetControl();
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 0, 0);
		data.widthHint = 92;
		data.horizontalIndent = 15;
		button.setLayoutData(data);

		button.addSelectionListener(attachExtractButtonSelectionListner());

	}

	/**
	 * Provides all the DB details
	 */
	private DatabaseParameterType getDatabaseConnectionDetails() {
		DatabaseParameterType parameterType = null;
		String databaseName = "";
		String hostName = "";
		String portNo= "";
		String jdbcName= "";
		String schemaName= "";
		String userName= "";
		String password= "";
		String sid= "";
		
		for (AbstractWidget textAbtractWgt : widgets) {
			if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.DATABASE_WIDGET_NAME)) {
				databaseName = getValue((String) textAbtractWgt.getProperties().get(Constants.DATABASE_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.HOST_WIDGET_NAME)) {
				hostName = getValue((String) textAbtractWgt.getProperties().get(Constants.HOST_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.PORT_WIDGET_NAME)) {
				portNo = getValue((String) textAbtractWgt.getProperties().get(Constants.PORT_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.JDBC_DRIVER_WIDGET_NAME)) {
				jdbcName = getValue((String) textAbtractWgt.getProperties().get(Constants.JDBC_DRIVER_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.SCHEMA_WIDGET_NAME)) {
				schemaName = getValue((String) textAbtractWgt.getProperties().get(Constants.SCHEMA_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.USER_NAME_WIDGET_NAME)) {
				userName = getValue((String) textAbtractWgt.getProperties().get(Constants.USER_NAME_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.PASSWORD_WIDGET_NAME)) {
				password = getValue((String) textAbtractWgt.getProperties().get(Constants.PASSWORD_WIDGET_NAME));
			}else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.ORACLE_SID_WIDGET_NAME)) {
				sid = getValue((String) textAbtractWgt.getProperties().get(Constants.ORACLE_SID_WIDGET_NAME));
			}
		}
		
		parameterType = new DatabaseParameterType.DatabaseBuilder(getComponentType(), hostName, 
				portNo, userName, password).jdbcName(jdbcName).schemaName(schemaName)
				.databaseName(databaseName).sid(sid).build();
		return parameterType;

	}
	
	private String getComponentType(){
		if(StringUtils.equalsIgnoreCase(getComponent().getType(), ORACLE)){
			return ORACLE;
		}else if(StringUtils.equalsIgnoreCase(getComponent().getType(), REDSHIFT)){
			return REDSHIFT;
		}else if(StringUtils.equalsIgnoreCase(getComponent().getType(), MYSQL)){
			return MYSQL;
		}else if(StringUtils.equalsIgnoreCase(getComponent().getType(), TERADATA)){
			return TERADATA;
		}
		return "";
	}
	
	private String getValue(String input) {
		if(ParameterUtil.isParameter(input)){
			return Utils.INSTANCE.getParamValue(input);
		}
		return input;
	}
	
	private void validateDatabaseParams(){
		List<String> oracleDatabaseValues = new ArrayList<String>();
		getDatabaseConnectionDetails();
		
		LinkedHashMap<String, Object> property = getProperties();
		databaseSelectionConfig = (DatabaseSelectionConfig) property.get(propertyName);
		
		if (((Button) tableNameRadioButton.getSWTWidgetControl()).getSelection()) {
			oracleDatabaseValues.add(getValue(databaseSelectionConfig.getTableName()));
		}else{
			WidgetUtility.createMessageBox(Messages.METASTORE_FORMAT_ERROR_FOR_SQL_QUERY,Messages.ERROR , SWT.ICON_INFORMATION);
		}
		if (oracleDatabaseValues != null && oracleDatabaseValues.size() > 0) {
			extractOracleMetaStoreDetails(oracleDatabaseValues);
		}
	}

	private void validateDatabaseFields(DatabaseParameterType parameterType){
		
		if(parameterType.getDataBaseType().equalsIgnoreCase(ORACLE)){
			if ( StringUtils.isEmpty(parameterType.getHostName())|| StringUtils.isEmpty(parameterType.getJdbcName())
				|| StringUtils.isEmpty(parameterType.getPortNo())|| StringUtils.isEmpty(parameterType.getUserName())
				|| StringUtils.isEmpty(parameterType.getSid())|| StringUtils.isEmpty(parameterType.getPassword())) {
			}
		}else{
			if (StringUtils.isEmpty(parameterType.getDatabaseName()) || StringUtils.isEmpty(parameterType.getHostName())
					|| StringUtils.isEmpty(parameterType.getJdbcName()) || StringUtils.isEmpty(parameterType.getPortNo())
					|| StringUtils.isEmpty(parameterType.getUserName()) || StringUtils.isEmpty(parameterType.getPassword())) {
			}
		}
	}
	
	/**
	 * Selection listener on Extract MetaStore button
	 * @return
	 */
	private SelectionAdapter attachExtractButtonSelectionListner() {
		
		SelectionAdapter adapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				validateDatabaseParams();
				}
			};

		return adapter;
	}
	
	/**
	 * Extracts the details from MetaStore for DB components
	 * @param oracleDatabaseValues
	 * @param host
	 * @param port_no
	 */
	private void extractOracleMetaStoreDetails(List<String> oracleDatabaseValues) {
		
String host = DataBaseUtility.getInstance().getServiceHost();
		
		if(null!=host&& StringUtils.isNotBlank(host)){
			
				DatabaseParameterType parameterType =  getDatabaseConnectionDetails();
				validateDatabaseFields(parameterType);
		
				HiveTableSchema databaseTableSchema = DataBaseUtility.getInstance()
					.extractDatabaseDetails(oracleDatabaseValues, parameterType,host);
			
				if (null != databaseTableSchema) {

					for (AbstractWidget abstractWgt : widgets) {
					
						if (abstractWgt.getProperty().getPropertyName().equalsIgnoreCase(Constants.SCHEMA_PROPERTY_NAME)) {
							abstractWgt.refresh(getComponentSchema(databaseTableSchema));
						}
					}
	
					WidgetUtility.createMessageBox(Messages.METASTORE_IMPORT_SUCCESS,Messages.INFORMATION , SWT.ICON_INFORMATION);
					propertyDialogButtonBar.enableApplyButton(true);
				}
		}
		else{
			WidgetUtility.createMessageBox(Messages.HOST_NAME_BLANK_ERROR,Messages.ERROR , SWT.ICON_ERROR);
			}
	
	}
	/**
	 * @param hiveTableSchema
	 * @return
	 */
	private Schema getComponentSchema(HiveTableSchema hiveTableSchema) {
		Schema schema = new Schema();
		List<GridRow> rows = new ArrayList<>();

		for (HiveTableSchemaField hsf : hiveTableSchema
				.getSchemaFields()) {
			BasicSchemaGridRow gridRow = new BasicSchemaGridRow();

			gridRow.setFieldName(hsf.getFieldName());
			gridRow.setDataTypeValue(hsf.getFieldType());
			gridRow.setPrecision(hsf.getPrecision());
			gridRow.setScale(hsf.getScale());
			gridRow.setDateFormat(hsf.getFormat());
			gridRow.setScaleType(GridWidgetCommonBuilder.getScaleTypeByValue(hsf.getScaleType()));
			gridRow.setDataType(GridWidgetCommonBuilder
					.getDataTypeByValue(hsf.getFieldType()));

			rows.add(gridRow);
		}

		schema.setGridRow(rows);
		return schema;
	}

	/**
	 * Validates the feild values
	 * @param value
	 * @return
	 */
	private boolean validateField(String value) {
		if (null != value && StringUtils.isNotBlank(value)) {
			return true;
		}
		return false;
	}

	/**
	 * Populates the data in the textBoxes 
	 */
	private void populateWidget() {

		if (null != databaseSelectionConfig) {

			if (databaseSelectionConfig.isTableName()) {
				((Button) tableNameRadioButton.getSWTWidgetControl()).setSelection(true);

				if (validateField(databaseSelectionConfig.getTableName())) {
					tableNameDecorator.hide();
					textBoxTableName.setText(databaseSelectionConfig.getTableName());
					Utils.INSTANCE.addMouseMoveListener(textBoxTableName, cursor);

				} else {
					tableNameDecorator.show();
				}

			} else {

				((Button) sqlQueryRadioButton.getSWTWidgetControl()).setSelection(true);
				if (validateField(databaseSelectionConfig.getSqlQuery())) {
					sqlQueryDecorator.hide();
					sqlQueryTextBox.setText(databaseSelectionConfig.getSqlQuery());
					Utils.INSTANCE.addMouseMoveListener(sqlQueryTextBox, cursor);
				} else {
					sqlQueryDecorator.show();
				}
				//TODO as above
				/*if (validateField(databaseSelectionConfig.getSqlQueryCounter())) {
					sqlQueryCountertextbox.setText(databaseSelectionConfig.getSqlQueryCounter());

				} */
					 
			}

		} else {
			tableNameDecorator.show();
			sqlQueryDecorator.show();
		}

	}

	/**
	 * Attach listener to textBox widgets
	 * @param textBoxWidget
	 * @param txtDecorator
	 */
	protected void attachListeners(AbstractELTWidget textBoxWidget, ControlDecoration txtDecorator) {
		ListenerHelper helper = prepareListenerHelper(txtDecorator);
		try {
			for (Listners listenerNameConstant : textBoxConfig.getListeners()) {
				IELTListener listener = listenerNameConstant.getListener();
				textBoxWidget.attachListener(listener, propertyDialogButtonBar, helper,
						textBoxWidget.getSWTWidgetControl());
			}
		} catch (Exception exception) {
			logger.error("Failed in attaching listeners to {}, {}", textBoxConfig.getName(), exception);
		}
	}

	/**
	 * Prepares listener helper 
	 * @param txtDecorator
	 * @return
	 */
	protected ListenerHelper prepareListenerHelper(ControlDecoration txtDecorator) {
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, txtDecorator);
		helper.put(HelperType.CURRENT_COMPONENT, getComponent());
		helper.put(HelperType.CHARACTER_LIMIT, textBoxConfig.getCharacterLimit());
		return helper;
	}
	
	/**
	 * Attach event change listener on TextBoxes
	 * @param textBoxWidget
	 */
	protected void attachListeners(AbstractELTWidget textBoxWidget) {
		try {

			textBoxWidget.attachListener(Listners.EVENT_CHANGE.getListener(), propertyDialogButtonBar, null,
					textBoxWidget.getSWTWidgetControl());
		} catch (Exception exception) {
			logger.error("Failed in attaching listeners to {}, {}", textBoxConfig.getName(), exception);
		}
	}
	
	/**
	 * Create Label on Stack layout composite
	 * @param labelName
	 * @param compositeWithStack
	 * @return
	 */
	private AbstractELTWidget createWidgetlabel(String labelName, ELTSubGroupCompositeWithStack compositeWithStack) {
		ELTDefaultLable label = new ELTDefaultLable(labelName).lableWidth(80);
		compositeWithStack.attachWidget(label);
		Label labelAlignment = ((Label) label.getSWTWidgetControl());
		GridData data = (GridData) labelAlignment.getLayoutData();
		data.verticalIndent = 5;

		return label;
	}
	
	/**
	 * Create TextBoxes on Stack layout composite
	 * @param labelName
	 * @param compositeWithStack
	 * @return
	 */
	private AbstractELTWidget createWidgetTextbox(String labelName, ELTSubGroupCompositeWithStack compositeWithStack) {

		AbstractELTWidget textboxWgt = new ELTDefaultTextBox()
				.grabExcessHorizontalSpace(textBoxConfig.getGrabExcessSpace());
		compositeWithStack.attachWidget(textboxWgt);
		Text textbox = ((Text) textboxWgt.getSWTWidgetControl());

		GridData data = (GridData) textbox.getLayoutData();
		data.horizontalIndent = 16;
		data.verticalIndent = 5;
		data.widthHint = 260;
		return textboxWgt;
	}
	
	/**
	 * Attach decorators to the TextBoxes 
	 * @param labelName
	 * @param textboxWgt
	 * @param txtDecorator
	 * @return
	 */
	private ControlDecoration attachDecoratorToTextbox(String labelName, AbstractELTWidget textboxWgt,
			ControlDecoration txtDecorator) {

		txtDecorator = WidgetUtility.addDecorator((Text) textboxWgt.getSWTWidgetControl(),
				Messages.bind(Messages.EMPTY_FIELD, labelName));
		txtDecorator.setMarginWidth(3);
		attachListeners(textboxWgt, txtDecorator);

		return txtDecorator;
	}
}