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

 
package hydrograph.ui.propertywindow.widgets.customwidgets;

import java.util.List;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.DropDownConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.EditButtonWithLabelConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.FilePathConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.OperationClassConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.RadioButtonConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.RuntimeConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.SchemaConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.SingleColumnGridConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.TextBoxWithLableConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;


/**
 * Helper class to provide configuration for customizing widgets.
 * Customizing can be of label, types of listeners etc. 
 * @author BITWISE
 *
 */
public class WidgetHelper {
	public static final WidgetHelper INSTANCE = new WidgetHelper();
	private WidgetHelper(){
	}

	public WidgetConfig getColumnNameConfig(){
		return populateSingleColumnGridConfig(Messages.LABEL_KEY_FIELDS, Constants.KEY_FIELDS_WINDOW_TITLE);
	}
	
	public WidgetConfig getPartitionKeysConfig(){
		return populateSingleColumnGridConfig(Messages.LABEL_PARTITION_KEYS, Constants.PARTITION_KEYS_WINDOW_TITLE);
	}
	
	public WidgetConfig getPartitionKeysConfigInputHive(){
		return populateSingleColumnGridConfig(Messages.LABEL_PARTITION_KEYS, Constants.PARTITION_KEYS_WINDOW_TITLE);
	}
	
	public WidgetConfig getOracleTestConnection(){
		return populateSingleColumnGridConfig(Messages.LABEL_PARTITION_KEYS, Constants.PARTITION_KEYS_WINDOW_TITLE);
	}

	public WidgetConfig getOperationFieldsConfig(){
		return populateSingleColumnGridConfig(Messages.LABEL_OPERATION_FIELDS, Constants.OPERATION_FIELDS_WINDOW_TITLE);
	}
	
	/**
	 * Configuration to customize text box as delimiter property 
	 */
	public WidgetConfig getDelimiterWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_DELIMITER);
		addDelimiterTextBoxListeners(textBoxConfig);
		textBoxConfig.setWidgetWidth(78);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as quote property 
	 */
	public WidgetConfig getQuoteWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_QUOTE);
		textBoxConfig.setWidgetWidth(78);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as count property 
	 */
	public WidgetConfig getCountWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_COUNT);
		textBoxConfig.getListeners().add(Listners.MODIFY);
		textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		textBoxConfig.getListeners().add(Listners.VERIFY_TEXT);
		textBoxConfig.getListeners().add(Listners.VERIFY_NUMERIC_OR_PARAMETER_FOCUS_IN);
		textBoxConfig.getListeners().add(Listners.VERIFY_NUMERIC_OR_PARAMETER_FOCUS_OUT);
		textBoxConfig.setWidgetWidth(78);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as batch property 
	 */
	public WidgetConfig getBatchWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Constants.BATCH);
		textBoxConfig.setCharacterLimit(2);
		addTextBoxListeners(textBoxConfig);
		textBoxConfig.getListeners().add(Listners.VERIFY_DIGIT_LIMIT_NUMERIC_LISTENER);
		textBoxConfig.setWidgetWidth(78);
		return textBoxConfig;
	}
	

	/**
	 * Configuration to customize text box as port property 
	 */
	public WidgetConfig getPortWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_PORT);
		textBoxConfig.setGrabExcessSpace(true);
		textBoxConfig.getListeners().add(Listners.PORT_FOCUS_IN);
		textBoxConfig.getListeners().add(Listners.PORT_FOCUS_OUT);
		textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		textBoxConfig.getListeners().add(Listners.MODIFY_NUMERIC_AND_PARAMETER);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as chunk size property 
	 */
	public WidgetConfig getTextBoxWithoutAnyValidationWidgetConfig(String label){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(label);
		textBoxConfig.setCharacterLimit(10);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		textBoxConfig.getListeners().add(Listners.VERIFY_NUMERIC);
		textBoxConfig.getListeners().add(Listners.VERIFY_DIGIT_LIMIT_NUMERIC_LISTENER);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as Table Name property 
	 */
	public WidgetConfig getTableNameWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_TABLE_NAME);
		textBoxConfig.setCharacterLimit(50);
		textBoxConfig.getListeners().add(Listners.VERIFY_CHARACTER_LIMIT_LISTENER);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	
	
	
	/**
	 * Configuration to customize text box as User Name property 
	 */
	public WidgetConfig getUserNameWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_USER_NAME);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as Password property 
	 */
	public WidgetConfig getPasswordWidgetConfig(boolean isMandatory){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_PWD);
		textBoxConfig.setGrabExcessSpace(true);
		textBoxConfig.setMandatory(isMandatory);
		if(isMandatory){
			addTextBoxListeners(textBoxConfig);
		}else{
			textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		}	
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as Database Name property 
	 */
	public WidgetConfig getDatabaseNameWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_DATABASE_NAME);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	/**
	 * Configuration to customize text box as SID Name property 
	 */
	public WidgetConfig getSIDNameWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_SID_NAME);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	
	
	/**
	 * Configuration to customize text box as External Table Path property 
	 */
	public FilePathConfig getExternalTablePathWidgetConfig(String label){
		FilePathConfig filePathConfig=getFilePathWidgetConfig(label);
		filePathConfig.setMandatory(false);
		return filePathConfig;
	}
	
	/*
	 * Configuration to customize text box as Oracle Schema Name property 
	 */
	public WidgetConfig getOracleSchemaWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_ORACLE_SCHEMA);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as Host Name property 
	 */
	public WidgetConfig getHostNameWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_HOST_NAME);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as Host Name property 
	 */
	public WidgetConfig getTemporaryWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.TEMPORARY_DIR_WIDGET_LABEL);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	

	public WidgetConfig getDBUrlWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_DB_URL);
		textBoxConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as noOfRecords property 
	 */
	public WidgetConfig getNoOfRecordsWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_NO_OF_RECORDS);
		textBoxConfig.getListeners().add(Listners.MODIFY);
		textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		textBoxConfig.getListeners().add(Listners.VERIFY_NUMERIC_OR_PARAMETER_FOCUS_IN);
		textBoxConfig.getListeners().add(Listners.VERIFY_NUMERIC_OR_PARAMETER_FOCUS_OUT);
		textBoxConfig.setWidgetWidth(78);
		return textBoxConfig;
	}
	
	/**
	 * Configuration for sequence widget
	 */
	public WidgetConfig getSequenceFieldWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Constants.SEQUENCE_FIELD);
		addTextBoxListeners(textBoxConfig);
		textBoxConfig.getListeners().add(Listners.VERIFY_SEQUENCE_FIELD_NAME_EXISTS);
		textBoxConfig.setWidgetWidth(180);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as count property 
	 */
	public WidgetConfig getInputCountWidgetConfig(String propertyLabel,int minimumPortCount){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(propertyLabel);
		List<Listners> listeners = textBoxConfig.getListeners();
		String portCount = "1";
		if (minimumPortCount > 0) {
			portCount = String.valueOf(minimumPortCount);
		}
		textBoxConfig.getOtherAttributes().put(HelperType.MINIMUM_PORT_COUNT.toString(), portCount);
		listeners.add(Listners.MODIFY);
		listeners.add(Listners.VERIFY_NUMERIC);
		listeners.add(Listners.JOIN_INPUT_COUNT);
		listeners.add(Listners.JOIN_INPUT_COUNT_FOCUS_OUT);
		textBoxConfig.setWidgetWidth(78);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize dropdown as safe property 
	 */
	public WidgetConfig getSafeWidgetConfig(){
		DropDownConfig dropDownConfig = populateTrueFalseConfig(Messages.LABEL_SAFE_PROPERTY);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	/**
	 * Configuration to customize dropdown as hasHeader property 
	 */
	public WidgetConfig getHasHeaderWidgetConfig(){
		DropDownConfig dropDownConfig =  populateTrueFalseConfig(Messages.LABEL_HAS_HEADER);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}

	/**
	 * Configuration to customize dropdown as overWrite property 
	 */
	public WidgetConfig getOverWriteWidgetConfig(){
		DropDownConfig dropDownConfig =  populateTrueFalseConfig(Messages.LABEL_OVERWRITE);
		addComboBoxListeners(dropDownConfig);
		dropDownConfig.getDropDownListeners().add(Listners.OVER_WRITE_LISTENER);
		return dropDownConfig;
	}
	
	/**
	 * Configuration to customize dropdown as strict property 
	 */
	public WidgetConfig getStrictWidgetConfig(){
		DropDownConfig dropDownConfig =  populateTrueFalseConfig(Messages.LABEL_STRICT);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	/**
	 * Configuration to customize dropdown as characterSet property 
	 */
	public WidgetConfig getCharacterSetWidgetConfig(){
		DropDownConfig dropDownConfig = new DropDownConfig();
		dropDownConfig.setName(Messages.LABEL_CHARACTER_SET);
		dropDownConfig.getItems().add(Constants.UTF_8);
		dropDownConfig.getItems().add(Constants.US_ASCII);
		dropDownConfig.getItems().add(Constants.ISO_8859_1);
		dropDownConfig.getItems().add(Constants.PARAMETER);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	
	/**
	 * Configuration to customize dropdown as characterSet property 
	 */
	public WidgetConfig getDatabaseListWidgetConfig(){
		DropDownConfig dropDownConfig = new DropDownConfig();
		dropDownConfig.setName(Messages.LABEL_DATABASE_LIST);
		dropDownConfig.getItems().add(Messages.ORACLE);
		dropDownConfig.getItems().add(Messages.REDSHIFT);
		dropDownConfig.getItems().add(Messages.MYSQL);
		dropDownConfig.getItems().add(Messages.TERADATA);
		dropDownConfig.getItems().add(Messages.PARAMETER);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	
	public WidgetConfig getDatabaseWidgetConfig(){
		DropDownConfig dropDownConfig = new DropDownConfig();
		dropDownConfig.setName(Messages.LABEL_DATABASE);
		dropDownConfig.getItems().add(Messages.ORACLE);
		dropDownConfig.getItems().add(Messages.REDSHIFT);
		dropDownConfig.getItems().add(Messages.MYSQL);
		dropDownConfig.getItems().add(Messages.TERADATA);
		dropDownConfig.getItems().add(Messages.PARAMETER);
		addComboBoxListeners(dropDownConfig);
		
		return dropDownConfig;
	}
	
	public WidgetConfig getJDBCDriverClassWidgetConfig(){
		DropDownConfig dropDownConfig = new DropDownConfig();
		dropDownConfig.setName(Messages.LABEL_DATABASE);
		dropDownConfig.getItems().add(Messages.ORACLE);
		dropDownConfig.getItems().add(Messages.REDSHIFT);
		dropDownConfig.getItems().add(Messages.MYSQL);
		dropDownConfig.getItems().add(Messages.TERADATA);
		dropDownConfig.getItems().add(Messages.OTHERS);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	/**
	 * Configuration to customize text box as JDBC Driver property 
	 */
	public WidgetConfig getJdbcDriverWidgetConfig(){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.DRIVER_NAME);
		textBoxConfig.setGrabExcessSpace(true);
		textBoxConfig.setEnabled(false);
		addTextBoxListeners(textBoxConfig);
		return textBoxConfig;
	}
	
	/**
	 * Configuration for operation class widget
	 */
	public WidgetConfig getOperationClassForFilterWidgetConfig() {
		OperationClassConfig operationClassConfig = new OperationClassConfig();
		operationClassConfig.setComponentName(Constants.FILTER);
		return operationClassConfig;
	}
	
	/**
	 * Configuration for operation class widget
	 */
	public WidgetConfig getOperationClassForPartitionByExpressionWidgetConfig() {
		OperationClassConfig operationClassConfig = new OperationClassConfig();
		operationClassConfig.setComponentName(Constants.PARTITION_BY_EXPRESSION);
		return operationClassConfig;
	}
	
	/**
	 * Configuration for Transform component operation class widget
	 */
	public WidgetConfig getOperationClassForTransformWidgetConfig(String componentName, String componentDisplayName,String windowTitle) {
		OperationClassConfig operationClassConfig = new OperationClassConfig();
		operationClassConfig.setComponentName(componentName);
		operationClassConfig.setComponentDisplayName(componentDisplayName);
		operationClassConfig.setWindowTitle(windowTitle);
		return operationClassConfig;
	}
	
	/**
	 * Configuration for table as Primary key table
	 */
	public WidgetConfig getPrimaryKeyWidgetConfig() {
		EditButtonWithLabelConfig buttonWithLabelConfig = new EditButtonWithLabelConfig();
		buttonWithLabelConfig.setName(Messages.LABEL_KEY_FIELDS);
		buttonWithLabelConfig.setWindowName(Messages.PRIMARY_COLUMN_KEY_WINDOW_NAME);
		return buttonWithLabelConfig;
	}
	
	/**
	 * Configuration for table as PrimaSecondary key table
	 */
	public WidgetConfig getSecondaryKeyWidgetConfig() {
		EditButtonWithLabelConfig buttonWithLabelConfig = new EditButtonWithLabelConfig();
		buttonWithLabelConfig.setName(Messages.LABEL_SECONDARY_KEYS);
		buttonWithLabelConfig.setWindowName(Messages.SECONDARY_COLUMN_KEY_WINDOW_NAME);
		return buttonWithLabelConfig;
	}
		
	private SingleColumnGridConfig populateSingleColumnGridConfig(String lable, String componentName) {
		SingleColumnGridConfig gridConfig = new SingleColumnGridConfig();
		gridConfig.setLabelName(lable);
		gridConfig.setComponentName(componentName);
		return gridConfig;		
	}
	
	private void addComboBoxListeners(DropDownConfig dropDownConfig) {
		List<Listners> dropDownListeners = dropDownConfig.getDropDownListeners();
		dropDownListeners.add(Listners.SELECTION);
		
		List<Listners> textBoxListeners = dropDownConfig.getTextBoxListeners();
		textBoxListeners.add(Listners.EVENT_CHANGE);
		textBoxListeners.add(Listners.VERIFY_TEXT);
		textBoxListeners.add(Listners.FOCUS_OUT);
		textBoxListeners.add(Listners.FOCUS_IN);
		textBoxListeners.add(Listners.MODIFY);
	}
	
	private void addTextBoxListeners(TextBoxWithLableConfig textBoxConfig) {
		List<Listners> listeners = textBoxConfig.getListeners();
		listeners.add(Listners.NORMAL_FOCUS_IN);
		listeners.add(Listners.NORMAL_FOCUS_OUT);
		listeners.add(Listners.EVENT_CHANGE);
		listeners.add(Listners.MODIFY);
	}
	
	private void addDelimiterTextBoxListeners(TextBoxWithLableConfig textBoxConfig) {
		List<Listners> listeners = textBoxConfig.getListeners();
		listeners.add(Listners.DELIMITER_FOCUS_IN);
		listeners.add(Listners.DELIMITER_FOCUS_OUT);
		listeners.add(Listners.EVENT_CHANGE);
		listeners.add(Listners.DELIMITER_MODIFY);
	}
	
	private DropDownConfig populateTrueFalseConfig(String name){
		DropDownConfig dropDownConfig = new DropDownConfig();
		dropDownConfig.setName(name);
		dropDownConfig.getItems().add(Constants.FALSE);
		dropDownConfig.getItems().add(Constants.TRUE);
		dropDownConfig.getItems().add(Constants.PARAMETER);
		return dropDownConfig;
	}
	
	public WidgetConfig getRunTimeWidgetConfig(String label,String windowLabel) {
		RuntimeConfig runtimeConfig = new RuntimeConfig();
		runtimeConfig.setLabel(label);
		runtimeConfig.setWindowLabel(windowLabel);
		return runtimeConfig;
	}
	
	public FilePathConfig getFilePathWidgetConfig(String label) {
		FilePathConfig filePathConfig= new FilePathConfig();
		filePathConfig.setLabel(Messages.FILE_PATH_LABEL);
		filePathConfig.setMandatory(true);
		filePathConfig.setfileExtension(new String[] {"*.*"});
		return filePathConfig;
	}
	
	public FilePathConfig getXMLFilePathWidgetConfig() {
		FilePathConfig filePathConfig= new FilePathConfig();
		filePathConfig.setLabel(Messages.FILE_PATH_LABEL);
		filePathConfig.setMandatory(true);
		filePathConfig.setfileExtension(new String[] {"*.xml"});
		filePathConfig.getListeners().add(Listners.XML_FILE_PATH_MODIFY);
		return filePathConfig;
		
	}


	/**
	 * 
	 *@return schema configuration for straight pull component
	 */
	public SchemaConfig getStraightPullSchemaConfig() {
		SchemaConfig schemaConfig=new SchemaConfig();
		schemaConfig.setDoPropagateONOK(false);
		return schemaConfig;
	}

	/**
	 * @return schema configuration for component other than straight pull
	 */
	public SchemaConfig getSchemaConfig() {
		SchemaConfig schemaConfig=new SchemaConfig();
		schemaConfig.setDoPropagateONOK(true);
		return schemaConfig;
	}
	
	public WidgetConfig getSelectWidgetConfig() {
		TextBoxWithLableConfig textBoxWithLableConfig = new TextBoxWithLableConfig();
		textBoxWithLableConfig.setGrabExcessSpace(true);
		addTextBoxListeners(textBoxWithLableConfig);
		return textBoxWithLableConfig;
	}

	public WidgetConfig getMatchValueWidgetConfig() {
		RadioButtonConfig radioConfig = new RadioButtonConfig();
		radioConfig.setWidgetDisplayNames(new String []{Constants.FIRST,Constants.LAST, Constants.ALL});
		radioConfig.setPropertyName(Messages.MATCH);
		return radioConfig;
	}

	public WidgetConfig getSelectInterfaceWithExportWidgetConfig() {
		RadioButtonConfig radioConfig = new RadioButtonConfig();
		radioConfig.setWidgetDisplayNames(new String []{Messages.STANDARD, Messages.FAST_EXPORT});
		radioConfig.setPropertyName(Messages.EXPORT_OPTIONS);
		return radioConfig;
	}
	
	public WidgetConfig getSelectInterfaceWithLoadWidgetConfig() {
		RadioButtonConfig radioConfig = new RadioButtonConfig();
		//TODO Currently we don't have Fast_load utility at Engine side. So commenting the code.
		radioConfig.setWidgetDisplayNames(new String []{Messages.STANDARD /*,Messages.FAST_LOAD*/});
		//radioConfig.getRadioButtonListners().add(Listners.VERIFY_FAST_LOAD_FOR_TERADATA);
		radioConfig.setPropertyName(Messages.LOAD_UTILITY_TYPE);
		return radioConfig;
	}
	
	public WidgetConfig getXPathQueryWidgetConfig() {
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LOOP_XPATH_QUERY);
		textBoxConfig.getListeners().add(Listners.MODIFY);
		textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		textBoxConfig.setGrabExcessSpace(true);
		return textBoxConfig;
	}

	public WidgetConfig getRootTagWidgetConfig() {
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.ROOT_TAG);
		textBoxConfig.getListeners().add(Listners.MODIFY);
		textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		textBoxConfig.setGrabExcessSpace(true);
		return textBoxConfig;
	}

	public WidgetConfig getRowTagWidgetConfig() {
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.ROW_TAG);
		textBoxConfig.getListeners().add(Listners.MODIFY);
		textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		textBoxConfig.setGrabExcessSpace(true);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize text box as Project Id
	 */
	public WidgetConfig getNumbericValueWidgetConfig(String fieldName){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(fieldName);
		textBoxConfig.setGrabExcessSpace(true);
		textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		textBoxConfig.getListeners().add(Listners.VERIFY_NUMERIC_AND_PARAMETER_FOR_DB_COMPONENTS);
		textBoxConfig.setWidgetWidth(78);
		return textBoxConfig;
	}
	
	/**
	 * Configuration to customize dropdown as ftp error property 
	 */
	public WidgetConfig getFailOnErrorWidgetConfig(){
		DropDownConfig dropDownConfig = new DropDownConfig();
		dropDownConfig.setName(Messages.FAIL_ON_ERROR);
		dropDownConfig.getItems().add(Constants.TRUE);
		dropDownConfig.getItems().add(Constants.FALSE);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	/**
	 * Configuration to customize dropdown as encoding error property 
	 */
	public WidgetConfig getEncodingWidgetConfig(){
		DropDownConfig dropDownConfig = new DropDownConfig();
		dropDownConfig.setName(Messages.LABEL_CHARACTER_SET);
		dropDownConfig.getItems().add(Constants.UTF_8);
		dropDownConfig.getItems().add(Constants.ISO_8859_1);
		dropDownConfig.getItems().add(Constants.PARAMETER);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	public WidgetConfig getProtocolSelectWidgetConfig() {
		TextBoxWithLableConfig textBoxWithLableConfig = new TextBoxWithLableConfig();
		textBoxWithLableConfig.setGrabExcessSpace(true);
		return textBoxWithLableConfig;
	}
	
	/*
	 * Configuration to customize dropdown as write mode property 
	 */
	public WidgetConfig getWriteModeWidgetConfig(){
		DropDownConfig dropDownConfig = new DropDownConfig();
		dropDownConfig.setName(Messages.LABEL_WRITE_MODE);
		dropDownConfig.getItems().add(Constants.APPEND);
		dropDownConfig.getItems().add(Constants.OVERWRITE);
		dropDownConfig.getItems().add(Constants.FAIL_IF_EXISTS);
		dropDownConfig.getItems().add(Constants.PARAMETER);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	/**
	 * Configuration to customize dropdown as Strip Leading Quote property 
	 */
	public WidgetConfig getStripLeadingQuoteWidgetConfig(){
		DropDownConfig dropDownConfig =  populateTrueFalseConfig(Messages.LABEL_STRIP_LEADING_QUOTE);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	/*
	 * Configuration to customize dropdown as Auto Size Column property 
	 */
	public WidgetConfig getAutoSizeColumnWidgetQuote(){
		DropDownConfig dropDownConfig =  populateTrueFalseConfig(Messages.LABEL_AUTO_SIZE_COLUMN);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	/**
	 * Configuration to customize dropdown as Abort On Error property 
	 */
	public WidgetConfig getAbortOnErrorWidgetQuote(){
		DropDownConfig dropDownConfig =  populateTrueFalseConfig(Messages.LABEL_ABORT_ON_ERROR);
		addComboBoxListeners(dropDownConfig);
		return dropDownConfig;
	}
	
	/**
	 * Configuration to customize text box as Worksheet Name property 
	 */
	public WidgetConfig getWorksheetNameWidgetConfig(boolean isMandatory){
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.LABEL_WORKSHEET_NAME);
		textBoxConfig.setGrabExcessSpace(true);
		textBoxConfig.setMandatory(isMandatory);
		if(isMandatory){
			addTextBoxListeners(textBoxConfig);
		}else{
			textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		}
		return textBoxConfig;
	}

	public FilePathConfig getExcelFilePathWidgetConfig(String fILE_PATH_LABEL) {
		FilePathConfig filePathConfig= new FilePathConfig();
		filePathConfig.setLabel(Messages.FILE_PATH_LABEL);
		filePathConfig.setMandatory(true);
		return filePathConfig;
	}

	public WidgetConfig getExcelFileNameWidgetConfig() {
		TextBoxWithLableConfig textBoxConfig = new TextBoxWithLableConfig();
		textBoxConfig.setName(Messages.FILE_NAME);
		textBoxConfig.getListeners().add(Listners.VERIFY_FILE_NAME);
		textBoxConfig.getListeners().add(Listners.EVENT_CHANGE);
		textBoxConfig.getListeners().add(Listners.EXCEL_FILE_NAME_FOCUS_IN);
		textBoxConfig.getListeners().add(Listners.EXCEL_FILE_NAME_FOCUS_OUT);
		textBoxConfig.setGrabExcessSpace(true);
		return textBoxConfig;
	}

	public WidgetConfig getSortExcelColumnWidgetConfig() {
		EditButtonWithLabelConfig buttonWithLabelConfig = new EditButtonWithLabelConfig();
		buttonWithLabelConfig.setName(Messages.SORT_COLUMNS);
		buttonWithLabelConfig.setWindowName(Messages.SORT_COLUMNS_WINDOW_LABEL);
		return buttonWithLabelConfig;
	}
	
}
